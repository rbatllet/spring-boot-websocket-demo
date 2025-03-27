package com.example.springbootwebsocket.controller;


import org.junit.jupiter.api.Test;
import com.example.springbootwebsocket.config.TestMessageSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(TestMessageSourceConfig.class)
@ActiveProfiles("test")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageSource messageSource;

    // We're using a real MessageSource now with our test properties

    @Test
    void testGetMessagesEnglish() throws Exception {
        // Test the endpoint
        mockMvc.perform(get("/api/messages")
                        .param("lang", "en")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"app.title\":\"Spring Boot WebSocket Chat\"")))
                .andExpect(content().string(containsString("\"ui.button.connect\":\"Connect\"")))
                .andExpect(content().string(containsString("\"ui.button.send\":\"Send\"")));
    }

    @Test
    void testGetPluralMessages() throws Exception {
        // Test plural forms
        mockMvc.perform(get("/api/messages")
                        .param("lang", "en")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"users.online.zero\":\"No users online\"")))
                .andExpect(content().string(containsString("\"users.online.one\":\"{0} user online\"")))
                .andExpect(content().string(containsString("\"users.online.other\":\"{0} users online\"")));
    }

    @Test
    void testGetMessagesWithDefaultLanguage() throws Exception {
        // Test the endpoint without specifying a language (should use default)
        mockMvc.perform(get("/api/messages")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"app.title\":\"Spring Boot WebSocket Chat\"")))
                .andExpect(content().string(containsString("\"ui.button.connect\":\"Connect\"")));
    }
}
