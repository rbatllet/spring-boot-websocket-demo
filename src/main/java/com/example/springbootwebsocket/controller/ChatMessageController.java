package com.example.springbootwebsocket.controller;

import com.example.springbootwebsocket.ChatMessage;
import com.example.springbootwebsocket.service.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for chat message history
 */
@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);
    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * Get all chat messages
     *
     * @return List of all chat messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        logger.debug("REST request to get all chat messages");
        return ResponseEntity.ok(chatMessageService.getAllMessages());
    }

    /**
     * Get only regular chat messages (excluding system messages)
     *
     * @return List of regular chat messages
     */
    @GetMapping("/messages/chat")
    public ResponseEntity<List<ChatMessage>> getChatMessages() {
        logger.debug("REST request to get regular chat messages");
        return ResponseEntity.ok(chatMessageService.getChatMessages());
    }

    /**
     * Get messages by type
     *
     * @param type The message type to filter by
     * @return List of messages of the specified type
     */
    @GetMapping("/messages/type/{type}")
    public ResponseEntity<List<ChatMessage>> getMessagesByType(@PathVariable ChatMessage.MessageType type) {
        logger.debug("REST request to get messages by type: {}", type);
        return ResponseEntity.ok(chatMessageService.getMessagesByType(type));
    }

    /**
     * Get messages by sender name
     *
     * @param name The sender name to filter by
     * @return List of messages from the specified sender
     */
    @GetMapping("/messages/sender/{name}")
    public ResponseEntity<List<ChatMessage>> getMessagesBySender(@PathVariable String name) {
        logger.debug("REST request to get messages by sender: {}", name);
        return ResponseEntity.ok(chatMessageService.getMessagesBySender(name));
    }
}
