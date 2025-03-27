package com.example.springbootwebsocket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the MessageController with the real MessageSource
 */
@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetMessagesEndpointWithEnglishLocale() throws Exception {
        mockMvc.perform(get("/api/messages")
                .param("lang", "en")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Test some key messages
                .andExpect(jsonPath("$.['app.title']").exists())
                .andExpect(jsonPath("$.['ui.button.connect']").exists())
                .andExpect(jsonPath("$.['ui.button.send']").exists())
                .andExpect(jsonPath("$.['ui.connection.connected']").exists())
                .andExpect(jsonPath("$.['ui.connection.disconnected']").exists());
    }

    @Test
    void testGetMessagesWithDefaultLocale() throws Exception {
        mockMvc.perform(get("/api/messages")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Test a specific common key
                .andExpect(jsonPath("$.['app.title']").value("Spring Boot WebSocket Chat"));
    }

    @Test 
    void testMessageFormatWithParameters() throws Exception {
        mockMvc.perform(get("/api/messages")
                .param("lang", "en")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Test messages with placeholders
                .andExpect(jsonPath("$.['chat.message.join']").value(containsString("{0}")))
                .andExpect(jsonPath("$.['chat.message.leave']").value(containsString("{0}")));
    }
    
    @Test
    void testPluralMessages() throws Exception {
        mockMvc.perform(get("/api/messages")
                .param("lang", "en")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Test plural forms
                .andExpect(jsonPath("$.['users.online.zero']").exists())
                .andExpect(jsonPath("$.['users.online.one']").exists())
                .andExpect(jsonPath("$.['users.online.other']").exists())
                .andExpect(jsonPath("$.['users.online.zero']").value("No users online"))
                .andExpect(jsonPath("$.['users.online.one']").value("{0} user online"))
                .andExpect(jsonPath("$.['users.online.other']").value("{0} users online"));
    }
}
