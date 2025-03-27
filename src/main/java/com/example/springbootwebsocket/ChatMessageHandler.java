package com.example.springbootwebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles WebSocket communication for the chat application
 */
@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionNames = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageUtils messageUtils;

    @Autowired
    public ChatMessageHandler(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    /**
     * Handles new WebSocket connections
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        logger.info("New WebSocket connection established: {}", session.getId());
        logger.info("Total active connections: {}", sessions.size());
        
        // Send individual user count message to the new session
        try {
            int userCount = sessions.size();
            ChatMessage countMessage = ChatMessage.createUserCountMessage(userCount);
            String serializedMessage = new ObjectMapper().writeValueAsString(countMessage);
            session.sendMessage(new TextMessage(serializedMessage));
        } catch (Exception e) {
            logger.error("Error sending user count to new session: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles WebSocket connection closures
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        String userName = sessionNames.remove(sessionId);
        sessions.remove(sessionId);
        
        logger.info("WebSocket connection closed: {} with status: {}", sessionId, status);
        logger.info("Total active connections: {}", sessions.size());
        
        // Notify other users that someone left the chat
        if (userName != null) {
            try {
                String leaveMessage = messageUtils.getMessage("chat.message.leave", userName);
                ChatMessage chatLeaveMessage = ChatMessage.createLeaveMessage(userName, leaveMessage);
                broadcastMessage(chatLeaveMessage);
                
                // Send updated user count to all clients
                broadcastUserCount();
            } catch (Exception e) {
                logger.error("Error sending leave message: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Handles incoming text messages
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String payload = message.getPayload();
            logger.debug("Received message from session {}: {}", session.getId(), payload);
            
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
            
            // Register username if not registered
            if (!sessionNames.containsKey(session.getId())) {
                sessionNames.put(session.getId(), chatMessage.getName());
                
                // Send welcome message to the new user
                String joinMessage = messageUtils.getMessage("chat.message.join", chatMessage.getName());
                ChatMessage chatJoinMessage = ChatMessage.createJoinMessage(chatMessage.getName(), joinMessage);
                broadcastMessage(chatJoinMessage);
                
                // Send updated user count to all clients
                broadcastUserCount();
            }
            
            // Broadcast the original message
            if (chatMessage.getType() == null) {
                chatMessage.setType(ChatMessage.MessageType.CHAT);
            }
            
            broadcastMessage(chatMessage);
            
        } catch (Exception e) {
            logger.error("Error handling message: {}", e.getMessage(), e);
            try {
                String errorMessage = messageUtils.getMessage("chat.message.error.processing");
                ChatMessage errorChatMessage = ChatMessage.createErrorMessage(errorMessage);
                String errorPayload = objectMapper.writeValueAsString(errorChatMessage);
                session.sendMessage(new TextMessage(errorPayload));
            } catch (IOException ex) {
                logger.error("Error sending error message: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Broadcasts a message to all connected clients
     */
    private void broadcastMessage(ChatMessage message) throws IOException {
        String serializedMessage = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(serializedMessage);
        
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    logger.error("Error sending message to session {}: {}", 
                            session.getId(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Handles WebSocket transport errors
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("Transport error for session {}: {}", session.getId(), exception.getMessage(), exception);
        try {
            if (session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        } catch (IOException e) {
            logger.error("Error closing session after transport error: {}", e.getMessage(), e);
        }
    }

    /**
     * Returns the current number of active sessions
     */
    /**
     * Broadcasts the current user count to all connected clients
     */
    private void broadcastUserCount() {
        try {
            int userCount = sessions.size();
            ChatMessage countMessage = ChatMessage.createUserCountMessage(userCount);
            broadcastMessage(countMessage);
        } catch (Exception e) {
            logger.error("Error broadcasting user count: {}", e.getMessage(), e);
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
