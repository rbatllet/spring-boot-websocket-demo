package com.example.springbootwebsocket.controller;

import com.example.springbootwebsocket.service.ChatMessageService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Test configuration for ChatMessageControllerTest
 * Provides mock beans for controller dependencies
 */
@TestConfiguration
public class ChatMessageControllerTestConfig {

    @Bean
    public ChatMessageService chatMessageService() {
        return mock(ChatMessageService.class);
    }
}
