package com.example.springbootwebsocket.repository;

import com.example.springbootwebsocket.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
public class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void testSaveAndFindMessage() {
        // Create a test message
        ChatMessage message = new ChatMessage();
        message.setName("TestUser");
        message.setMessage("Hello, this is a test");
        message.setTimestamp("2025-04-01T09:30:00");
        message.setType(ChatMessage.MessageType.CHAT);

        // Save the message
        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Verify that it was saved correctly
        assertNotNull(savedMessage.getId());
        assertEquals("TestUser", savedMessage.getName());
        assertEquals("Hello, this is a test", savedMessage.getMessage());

        // Retrieve the message by ID
        ChatMessage retrievedMessage = chatMessageRepository.findById(savedMessage.getId()).orElse(null);
        assertNotNull(retrievedMessage);
        assertEquals(savedMessage.getId(), retrievedMessage.getId());
    }

    @Test
    public void testFindByTypeOrderByTimestampDesc() {
        // Create messages of different types
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setName("User1");
        chatMessage.setMessage("Chat message");
        chatMessage.setTimestamp("2025-04-01T09:31:00");
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(chatMessage);

        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setName("User2");
        joinMessage.setMessage("has joined the chat");
        joinMessage.setTimestamp("2025-04-01T09:32:00");
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessageRepository.save(joinMessage);

        // Find messages by type
        List<ChatMessage> chatMessages = chatMessageRepository.findByTypeOrderByTimestampDesc(ChatMessage.MessageType.CHAT);
        assertEquals(1, chatMessages.size());
        assertEquals("User1", chatMessages.get(0).getName());

        List<ChatMessage> joinMessages = chatMessageRepository.findByTypeOrderByTimestampDesc(ChatMessage.MessageType.JOIN);
        assertEquals(1, joinMessages.size());
        assertEquals("User2", joinMessages.get(0).getName());
    }

    @Test
    public void testFindByNameOrderByTimestampDesc() {
        // Create messages from different users
        ChatMessage message1 = new ChatMessage();
        message1.setName("Alice");
        message1.setMessage("Alice's message");
        message1.setTimestamp("2025-04-01T09:33:00");
        message1.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(message1);

        ChatMessage message2 = new ChatMessage();
        message2.setName("Bob");
        message2.setMessage("Bob's message");
        message2.setTimestamp("2025-04-01T09:34:00");
        message2.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(message2);

        ChatMessage message3 = new ChatMessage();
        message3.setName("Alice");
        message3.setMessage("Another message from Alice");
        message3.setTimestamp("2025-04-01T09:35:00");
        message3.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(message3);

        // Find messages by name
        List<ChatMessage> aliceMessages = chatMessageRepository.findByNameOrderByTimestampDesc("Alice");
        assertEquals(2, aliceMessages.size());

        List<ChatMessage> bobMessages = chatMessageRepository.findByNameOrderByTimestampDesc("Bob");
        assertEquals(1, bobMessages.size());
        assertEquals("Bob's message", bobMessages.get(0).getMessage());
    }

    @Test
    public void testFindAllByOrderByTimestampDesc() {
        // Create multiple messages with different timestamps
        ChatMessage message1 = new ChatMessage();
        message1.setName("User1");
        message1.setMessage("Oldest message");
        message1.setTimestamp("2025-04-01T09:30:00");
        message1.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(message1);

        ChatMessage message2 = new ChatMessage();
        message2.setName("User2");
        message2.setMessage("Middle message");
        message2.setTimestamp("2025-04-01T09:35:00");
        message2.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(message2);

        ChatMessage message3 = new ChatMessage();
        message3.setName("User3");
        message3.setMessage("Most recent message");
        message3.setTimestamp("2025-04-01T09:40:00");
        message3.setType(ChatMessage.MessageType.CHAT);
        chatMessageRepository.save(message3);

        // Find all messages ordered by timestamp
        List<ChatMessage> allMessages = chatMessageRepository.findAllByOrderByTimestampDesc();
        assertEquals(3, allMessages.size());
        
        // Verify they are correctly ordered (most recent first)
        assertEquals("Most recent message", allMessages.get(0).getMessage());
        assertEquals("Middle message", allMessages.get(1).getMessage());
        assertEquals("Oldest message", allMessages.get(2).getMessage());
    }
}
