package com.example.springbootwebsocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChatMessageHandlerTest {

    private ChatMessageHandler chatMessageHandler;
    private WebSocketSession session1;
    private WebSocketSession session2;
    private TextMessage textMessage;

    @BeforeEach
    void setUp() {
        chatMessageHandler = new ChatMessageHandler();
        
        // Mock WebSocketSessions
        session1 = mock(WebSocketSession.class);
        session2 = mock(WebSocketSession.class);
        
        when(session1.getId()).thenReturn("session1");
        when(session2.getId()).thenReturn("session2");
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        
        // Create a test message
        textMessage = new TextMessage("{\"name\":\"TestUser\",\"message\":\"Hello World!\"}");
    }

    @Test
    void testConnectionEstablished() throws Exception {
        // Test that a session is added when connection is established
        chatMessageHandler.afterConnectionEstablished(session1);
        assertEquals(1, chatMessageHandler.getActiveSessionCount());
        
        chatMessageHandler.afterConnectionEstablished(session2);
        assertEquals(2, chatMessageHandler.getActiveSessionCount());
    }

    @Test
    void testConnectionClosed() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        assertEquals(2, chatMessageHandler.getActiveSessionCount());
        
        // Test that a session is removed when connection is closed
        chatMessageHandler.afterConnectionClosed(session1, CloseStatus.NORMAL);
        assertEquals(1, chatMessageHandler.getActiveSessionCount());
    }

    @Test
    void testHandleMessage() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Test that message is broadcast to all sessions
        chatMessageHandler.handleMessage(session1, textMessage);
        
        // Verify that the message was sent to both sessions
        verify(session1, times(1)).sendMessage(textMessage);
        verify(session2, times(1)).sendMessage(textMessage);
    }

    @Test
    void testHandleMessageWithClosedSession() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Make session2 closed
        when(session2.isOpen()).thenReturn(false);
        
        // Test that message is only sent to open sessions
        chatMessageHandler.handleMessage(session1, textMessage);
        
        // Verify that the message was sent only to session1
        verify(session1, times(1)).sendMessage(textMessage);
        verify(session2, never()).sendMessage(textMessage);
    }

    @Test
    void testHandleMessageWithException() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Make session2 throw exception when sending message
        doThrow(new IOException("Test exception")).when(session2).sendMessage(any());
        
        // Test that exception doesn't break the broadcast
        chatMessageHandler.handleMessage(session1, textMessage);
        
        // Verify that the message was sent to session1 despite exception in session2
        verify(session1, times(1)).sendMessage(textMessage);
    }

    @Test
    void testHandleTransportError() throws Exception {
        // Add session
        chatMessageHandler.afterConnectionEstablished(session1);
        
        // Test transport error handling
        Exception testException = new RuntimeException("Test transport error");
        chatMessageHandler.handleTransportError(session1, testException);
        
        // Verify that the session was closed with SERVER_ERROR status
        verify(session1, times(1)).close(CloseStatus.SERVER_ERROR);
    }
}
