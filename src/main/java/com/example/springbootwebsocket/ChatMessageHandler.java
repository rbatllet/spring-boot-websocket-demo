package com.example.springbootwebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.springbootwebsocket.service.ChatMessageService;
import com.example.springbootwebsocket.MessageUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles WebSocket communication for the chat application
 */
@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionNames = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatMessageHandler(MessageUtils messageUtils, ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
        // messageUtils is not used but kept for backward compatibility
    }

    /**
     * Handles new WebSocket connections
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
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
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        logger.info("WebSocket connection closed: {} with status: {}", session.getId(), status);
        
        try {
            // Remove session from active sessions
            sessions.remove(session.getId());
            
            // Get username associated with this session
            String username = sessionNames.get(session.getId());
            sessionNames.remove(session.getId());
            
            // Only broadcast leave message if username was registered
            if (username != null) {
                // Create a leave message directly without using MessageUtils
                String leaveMessage = username + " has left the chat";
                ChatMessage chatLeaveMessage = ChatMessage.createLeaveMessage(username, leaveMessage);
                
                // Save the leave message to the database
                chatMessageService.saveMessage(chatLeaveMessage);
                
                // Broadcast the leave message
                broadcastMessage(chatLeaveMessage);
            }
            
            // Update user count
            logger.info("Total active connections: {}", sessions.size());
            broadcastUserCount();
        } catch (Exception e) {
            logger.error("Error handling connection closure: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles incoming text messages
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            String payload = message.getPayload();
            logger.debug("Received message from session {}: {}", session.getId(), payload);
            
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
            
            // Register username if not registered
            if (!sessionNames.containsKey(session.getId())) {
                sessionNames.put(session.getId(), chatMessage.getName());
                
                // Only create a join message if the incoming message is not already a JOIN message
                if (chatMessage.getType() != ChatMessage.MessageType.JOIN) {
                    // Send welcome message to the new user
                    String joinMessage = chatMessage.getName() + " has joined the chat";
                    ChatMessage chatJoinMessage = ChatMessage.createJoinMessage(chatMessage.getName(), joinMessage);
                    
                    // Save the join message to the database
                    chatMessageService.saveMessage(chatJoinMessage);
                    
                    broadcastMessage(chatJoinMessage);
                } else {
                    // If it's already a JOIN message, just save it and broadcast it
                    chatMessageService.saveMessage(chatMessage);
                    broadcastMessage(chatMessage);
                }
                
                // Send updated user count to all clients
                broadcastUserCount();
                return; // Return early to avoid broadcasting the original message again
            }
            
            // Broadcast the original message
            if (chatMessage.getType() == null) {
                chatMessage.setType(ChatMessage.MessageType.CHAT);
            }
            
            // Only persist actual chat messages, not system messages like USER_COUNT
            if (chatMessage.getType() == ChatMessage.MessageType.CHAT) {
                // Save the chat message to the database
                chatMessageService.saveMessage(chatMessage);
            }
            
            broadcastMessage(chatMessage);
            
        } catch (Exception e) {
            logger.error("Error handling message: {}", e.getMessage(), e);
            try {
                String errorMessage = "Error processing message";
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
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
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
