package com.example.springbootwebsocket.repository;

import com.example.springbootwebsocket.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for handling ChatMessage entity persistence
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    /**
     * Find all chat messages ordered by timestamp (newest first)
     * 
     * @return List of chat messages
     */
    List<ChatMessage> findAllByOrderByTimestampDesc();
    
    /**
     * Find chat messages by type
     * 
     * @param type The message type to filter by
     * @return List of chat messages of the specified type
     */
    List<ChatMessage> findByTypeOrderByTimestampDesc(ChatMessage.MessageType type);
    
    /**
     * Find chat messages by sender name
     * 
     * @param name The sender name to filter by
     * @return List of chat messages from the specified sender
     */
    List<ChatMessage> findByNameOrderByTimestampDesc(String name);
}
