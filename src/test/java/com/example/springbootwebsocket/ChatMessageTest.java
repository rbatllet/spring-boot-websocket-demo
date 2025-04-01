package com.example.springbootwebsocket;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void testDefaultConstructor() {
        // Test default constructor sets timestamp
        ChatMessage message = new ChatMessage();
        assertNotNull(message.getTimestamp());
    }

    @Test
    void testConstructorWithParams() {
        // Test constructor with parameters
        ChatMessage message = new ChatMessage("TestUser", "Hello", ChatMessage.MessageType.CHAT);
        
        assertEquals("TestUser", message.getName());
        assertEquals("Hello", message.getMessage());
        assertEquals(ChatMessage.MessageType.CHAT, message.getType());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void testCreateChatMessage() {
        // Test static factory method for chat messages
        ChatMessage message = ChatMessage.createChatMessage("TestUser", "Hello");
        
        assertEquals("TestUser", message.getName());
        assertEquals("Hello", message.getMessage());
        assertEquals(ChatMessage.MessageType.CHAT, message.getType());
    }

    @Test
    void testCreateJoinMessage() {
        // Test static factory method for join messages
        String joinMessageText = "TestUser has joined the chat";
        ChatMessage message = ChatMessage.createJoinMessage("TestUser", joinMessageText);
        
        assertEquals("TestUser", message.getName());
        assertEquals(joinMessageText, message.getMessage());
        assertEquals(ChatMessage.MessageType.JOIN, message.getType());
    }

    @Test
    void testCreateLeaveMessage() {
        // Test static factory method for leave messages
        String leaveMessageText = "TestUser has left the chat";
        ChatMessage message = ChatMessage.createLeaveMessage("TestUser", leaveMessageText);
        
        assertEquals("TestUser", message.getName());
        assertEquals(leaveMessageText, message.getMessage());
        assertEquals(ChatMessage.MessageType.LEAVE, message.getType());
    }

    @Test
    void testCreateErrorMessage() {
        // Test static factory method for error messages
        ChatMessage message = ChatMessage.createErrorMessage("Error message");
        
        assertEquals("System", message.getName());
        assertEquals("Error message", message.getMessage());
        assertEquals(ChatMessage.MessageType.ERROR, message.getType());
    }
    
    @Test
    void testCreateUserCountMessage() {
        // Test static factory method for user count messages
        ChatMessage message = ChatMessage.createUserCountMessage(5);
        
        assertEquals("System", message.getName());
        assertEquals("", message.getMessage()); // Message is now empty
        assertEquals(5, message.getCount()); // Check the count field instead
        assertEquals(ChatMessage.MessageType.USER_COUNT, message.getType());
    }

    @Test
    void testGettersAndSetters() {
        // Test getters and setters
        ChatMessage message = new ChatMessage();
        
        message.setName("TestUser");
        message.setMessage("Hello");
        message.setType(ChatMessage.MessageType.CHAT);
        message.setTimestamp("2023-01-01T12:00:00Z");
        
        assertEquals("TestUser", message.getName());
        assertEquals("Hello", message.getMessage());
        assertEquals(ChatMessage.MessageType.CHAT, message.getType());
        assertEquals("2023-01-01T12:00:00Z", message.getTimestamp());
    }
}
