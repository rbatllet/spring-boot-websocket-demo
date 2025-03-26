package com.example.springbootwebsocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageHandler.class);
    private final List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        webSocketSessions.add(session);
        logger.info("New WebSocket connection established: {}", session.getId());
        logger.info("Total active connections: {}", webSocketSessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        webSocketSessions.remove(session);
        logger.info("WebSocket connection closed: {} with status: {}", session.getId(), status);
        logger.info("Total active connections: {}", webSocketSessions.size());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            logger.debug("Received message from session {}: {}", session.getId(), message.getPayload());
            broadcastMessage(message);
        } catch (Exception e) {
            logger.error("Error handling message: {}", e.getMessage(), e);
            session.sendMessage(new TextMessage("{\"error\":\"Error processing message\"}"));
        }
    }

    private void broadcastMessage(WebSocketMessage<?> message) {
        for (WebSocketSession webSocketSession : webSocketSessions) {
            try {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(message);
                }
            } catch (IOException e) {
                logger.error("Error sending message to session {}: {}", 
                        webSocketSession.getId(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Transport error for session {}: {}", session.getId(), exception.getMessage(), exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    public int getActiveSessionCount() {
        return webSocketSessions.size();
    }
}
