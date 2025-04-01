package com.example.springbootwebsocket.controller;

import com.example.springbootwebsocket.ChatMessage;
import com.example.springbootwebsocket.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatMessageController.class)
@ActiveProfiles("test")
@Import(ChatMessageControllerTestConfig.class)
public class ChatMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatMessageService chatMessageService;

    private List<ChatMessage> mockMessages;

    @BeforeEach
    public void setup() {
        // Create test messages
        ChatMessage message1 = new ChatMessage();
        message1.setId(1L);
        message1.setName("User1");
        message1.setMessage("Message 1");
        message1.setTimestamp("2025-04-01T09:00:00");
        message1.setType(ChatMessage.MessageType.CHAT);

        ChatMessage message2 = new ChatMessage();
        message2.setId(2L);
        message2.setName("User2");
        message2.setMessage("Message 2");
        message2.setTimestamp("2025-04-01T09:05:00");
        message2.setType(ChatMessage.MessageType.CHAT);

        ChatMessage message3 = new ChatMessage();
        message3.setId(3L);
        message3.setName("User3");
        message3.setMessage("has joined the chat");
        message3.setTimestamp("2025-04-01T09:10:00");
        message3.setType(ChatMessage.MessageType.JOIN);

        mockMessages = Arrays.asList(message1, message2, message3);
    }

    @Test
    public void testGetAllMessages() throws Exception {
        when(chatMessageService.getAllMessages()).thenReturn(mockMessages);

        mockMvc.perform(get("/api/chat/messages")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("User1")))
                .andExpect(jsonPath("$[1].name", is("User2")))
                .andExpect(jsonPath("$[2].name", is("User3")));
    }

    @Test
    public void testGetChatMessages() throws Exception {
        List<ChatMessage> chatMessages = Arrays.asList(mockMessages.get(0), mockMessages.get(1));
        when(chatMessageService.getChatMessages()).thenReturn(chatMessages);

        mockMvc.perform(get("/api/chat/messages/chat")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("CHAT")))
                .andExpect(jsonPath("$[1].type", is("CHAT")));
    }

    @Test
    public void testGetMessagesByType() throws Exception {
        List<ChatMessage> joinMessages = Arrays.asList(mockMessages.get(2));
        when(chatMessageService.getMessagesByType(ChatMessage.MessageType.JOIN)).thenReturn(joinMessages);

        mockMvc.perform(get("/api/chat/messages/type/JOIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("JOIN")))
                .andExpect(jsonPath("$[0].name", is("User3")));
    }

    @Test
    public void testGetMessagesBySender() throws Exception {
        List<ChatMessage> user1Messages = Arrays.asList(mockMessages.get(0));
        when(chatMessageService.getMessagesBySender("User1")).thenReturn(user1Messages);

        mockMvc.perform(get("/api/chat/messages/sender/User1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("User1")));
    }
}
