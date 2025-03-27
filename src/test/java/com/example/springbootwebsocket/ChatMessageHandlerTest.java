package com.example.springbootwebsocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChatMessageHandlerTest {

    private ChatMessageHandler chatMessageHandler;
    private MessageUtils messageUtils;
    private WebSocketSession session1;
    private WebSocketSession session2;
    private TextMessage textMessage;

    @BeforeEach
    void setUp() {
        // Mock MessageUtils
        messageUtils = mock(MessageUtils.class);
        when(messageUtils.getMessage(eq("chat.message.join"), any()))
            .thenReturn("TestUser has joined the chat");
        when(messageUtils.getMessage(eq("chat.message.leave"), any()))
            .thenReturn("TestUser has left the chat");
        when(messageUtils.getMessage(eq("chat.message.error.processing")))
            .thenReturn("Error processing message");
        
        // Create the handler with mocked dependencies
        chatMessageHandler = new ChatMessageHandler(messageUtils);
        
        // Mock WebSocketSessions
        session1 = mock(WebSocketSession.class);
        session2 = mock(WebSocketSession.class);
        
        when(session1.getId()).thenReturn("session1");
        when(session2.getId()).thenReturn("session2");
        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);
        
        // Create a test message
        textMessage = new TextMessage("{\"name\":\"TestUser\",\"message\":\"Hello World!\",\"type\":\"CHAT\"}");
    }

    @Test
    void testConnectionEstablished() throws Exception {
        // Test that a session is added when connection is established
        chatMessageHandler.afterConnectionEstablished(session1);
        assertEquals(1, chatMessageHandler.getActiveSessionCount());
        
        // Verify that a user count message is sent to the new session
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session1, atLeastOnce()).sendMessage(messageCaptor.capture());
        
        // Check that the message contains the user count
        boolean foundUserCountMessage = false;
        for (TextMessage msg : messageCaptor.getAllValues()) {
            if (msg.getPayload().contains("USER_COUNT")) {
                foundUserCountMessage = true;
                break;
            }
        }
        assertEquals(true, foundUserCountMessage);
        
        chatMessageHandler.afterConnectionEstablished(session2);
        assertEquals(2, chatMessageHandler.getActiveSessionCount());
    }

    @Test
    void testConnectionClosed() throws Exception {
        // Add sessions and register usernames
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Register a username by sending a message first
        TextMessage registerMessage = new TextMessage("{\"name\":\"TestUser\",\"message\":\"Hello\",\"type\":\"CHAT\"}");
        chatMessageHandler.handleTextMessage(session1, registerMessage);
        
        // Test that a leave message is sent when a session is closed
        chatMessageHandler.afterConnectionClosed(session1, CloseStatus.NORMAL);
        
        // Verify a leave message was broadcast (session2 should receive it)
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session2, atLeastOnce()).sendMessage(messageCaptor.capture());
        
        // Session2 receives both the original message and the leave message
        boolean foundLeaveMessage = false;
        for (TextMessage msg : messageCaptor.getAllValues()) {
            if (msg.getPayload().contains("LEAVE")) {
                foundLeaveMessage = true;
                break;
            }
        }
        
        assertEquals(true, foundLeaveMessage);
        assertEquals(1, chatMessageHandler.getActiveSessionCount());
    }

    @Test
    void testHandleTextMessage() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Test that message is broadcast to all sessions
        chatMessageHandler.handleTextMessage(session1, textMessage);
        
        // Verify both sessions receive the message
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session1, atLeastOnce()).sendMessage(messageCaptor.capture());
        verify(session2, atLeastOnce()).sendMessage(messageCaptor.capture());
    }

    @Test
    void testHandleTextMessageWithClosedSession() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Clear invocations to reset the verification state after setup
        clearInvocations(session1, session2);
        
        // Make session2 closed
        when(session2.isOpen()).thenReturn(false);
        
        // Test that message is only sent to open sessions
        chatMessageHandler.handleTextMessage(session1, textMessage);
        
        // Verify only session1 receives the message
        verify(session1, atLeastOnce()).sendMessage(any(TextMessage.class));
        verify(session2, never()).sendMessage(any(TextMessage.class));
    }

    /**
     * Test that exception in one client doesn't break the broadcast to others
     * We explicitly mark this as expected in the JavaDoc to clarify that 
     * the error logs are normal and expected
     */
    @Test
    void testHandleTextMessageWithException() throws Exception {
        // Add sessions
        chatMessageHandler.afterConnectionEstablished(session1);
        chatMessageHandler.afterConnectionEstablished(session2);
        
        // Make session2 throw exception when sending message
        // This WILL cause error logs in the console - THIS IS EXPECTED
        doThrow(new IOException("Test exception - THIS ERROR LOG IS EXPECTED")).when(session2).sendMessage(any());
        
        // Test that exception doesn't break the broadcast
        chatMessageHandler.handleTextMessage(session1, textMessage);
        
        // Verify session1 still receives messages despite exception with session2
        verify(session1, atLeastOnce()).sendMessage(any(TextMessage.class));
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
