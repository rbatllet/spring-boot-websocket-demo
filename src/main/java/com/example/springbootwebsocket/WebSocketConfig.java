package com.example.springbootwebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageHandler chatMessageHandler;

    @Autowired
    public WebSocketConfig(ChatMessageHandler chatMessageHandler) {
        this.chatMessageHandler = chatMessageHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(chatMessageHandler, "/chat")
                .setAllowedOrigins("*"); // For development - restrict in production
    }

}