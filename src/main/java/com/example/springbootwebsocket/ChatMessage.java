package com.example.springbootwebsocket;

import java.time.Instant;

/**
 * Represents a chat message in the WebSocket communication
 */
public class ChatMessage {
    private String name;
    private String message;
    private String timestamp;
    private MessageType type;

    /**
     * The type of message
     */
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        ERROR,
        USER_COUNT
    }

    /**
     * Default constructor for deserialization
     */
    public ChatMessage() {
        this.timestamp = Instant.now().toString();
    }

    /**
     * Constructor with all fields
     *
     * @param name    The sender's name
     * @param message The message content
     * @param type    The message type
     */
    public ChatMessage(String name, String message, MessageType type) {
        this.name = name;
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.type = type;
    }

    // Static factory methods for creating different message types

    /**
     * Create a standard chat message
     *
     * @param name    Sender name
     * @param message Message content
     * @return A new ChatMessage instance
     */
    public static ChatMessage createChatMessage(String name, String message) {
        return new ChatMessage(name, message, MessageType.CHAT);
    }

    /**
     * Create a join notification message
     *
     * @param name User name who joined
     * @param message Message content (usually from message bundle)
     * @return A new ChatMessage instance
     */
    public static ChatMessage createJoinMessage(String name, String message) {
        return new ChatMessage(name, message, MessageType.JOIN);
    }

    /**
     * Create a leave notification message
     *
     * @param name User name who left
     * @param message Message content (usually from message bundle)
     * @return A new ChatMessage instance
     */
    public static ChatMessage createLeaveMessage(String name, String message) {
        return new ChatMessage(name, message, MessageType.LEAVE);
    }

    /**
     * Create an error message
     *
     * @param errorMessage Error description
     * @return A new ChatMessage instance
     */
    public static ChatMessage createErrorMessage(String errorMessage) {
        return new ChatMessage("System", errorMessage, MessageType.ERROR);
    }
    
    /**
     * Create a user count message
     * 
     * @param count Number of active users
     * @return A new ChatMessage instance
     */
    public static ChatMessage createUserCountMessage(int count) {
        return new ChatMessage("System", String.valueOf(count), MessageType.USER_COUNT);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
