package com.example.springbootwebsocket.service;

import com.example.springbootwebsocket.ChatMessage;
import com.example.springbootwebsocket.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling chat message operations
 */
@Service
public class ChatMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageService.class);
    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Save a chat message to the database
     *
     * @param chatMessage The message to save
     * @return The saved message with its generated ID
     */
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        logger.debug("Saving chat message: {}", chatMessage.getMessage());
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * Get all chat messages ordered by timestamp (newest first)
     *
     * @return List of all chat messages
     */
    public List<ChatMessage> getAllMessages() {
        logger.debug("Retrieving all chat messages");
        return chatMessageRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Get chat messages by type
     *
     * @param type The message type to filter by
     * @return List of chat messages of the specified type
     */
    public List<ChatMessage> getMessagesByType(ChatMessage.MessageType type) {
        logger.debug("Retrieving chat messages by type: {}", type);
        return chatMessageRepository.findByTypeOrderByTimestampDesc(type);
    }

    /**
     * Get chat messages by sender name
     *
     * @param name The sender name to filter by
     * @return List of chat messages from the specified sender
     */
    public List<ChatMessage> getMessagesBySender(String name) {
        logger.debug("Retrieving chat messages by sender: {}", name);
        return chatMessageRepository.findByNameOrderByTimestampDesc(name);
    }

    /**
     * Get only regular chat messages (excluding system messages)
     *
     * @return List of regular chat messages
     */
    public List<ChatMessage> getChatMessages() {
        logger.debug("Retrieving regular chat messages");
        return chatMessageRepository.findByTypeOrderByTimestampDesc(ChatMessage.MessageType.CHAT);
    }
}
