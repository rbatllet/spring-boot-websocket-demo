package com.example.springbootwebsocket;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a chat message in the WebSocket communication
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private String message;
    
    private String timestamp;
    
    private int count;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
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
        ChatMessage message = new ChatMessage("System", "", MessageType.USER_COUNT);
        message.count = count;
        return message;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
