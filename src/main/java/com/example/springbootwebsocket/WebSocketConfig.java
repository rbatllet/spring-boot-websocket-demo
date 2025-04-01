package com.example.springbootwebsocket;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * Configuration class for WebSocket endpoints and settings
 */
@Configuration
@EnableWebSocket
@ConditionalOnWebApplication(type = Type.SERVLET)  // Only activated in full web applications
@Profile("!test")  // Not activated in test profile
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageHandler chatMessageHandler;
    
    @Value("${websocket.endpoint:/chat}")
    private String endpoint;
    
    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;
    
    @Value("${websocket.max-text-message-size:8192}")
    private Integer maxTextMessageSize;
    
    @Value("${websocket.max-binary-message-size:65536}")
    private Integer maxBinaryMessageSize;
    
    @Value("${websocket.max-session-idle-timeout:600000}")
    private Long maxSessionIdleTimeout;

    public WebSocketConfig(ChatMessageHandler chatMessageHandler) {
        this.chatMessageHandler = chatMessageHandler;
    }

    /**
     * Registers WebSocket handlers with their URL paths
     */
    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(chatMessageHandler, endpoint)
                .setAllowedOrigins(allowedOrigins); // For development - restrict in production
    }
    
    /**
     * Configures WebSocket container settings like message sizes and timeouts
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(maxTextMessageSize);
        container.setMaxBinaryMessageBufferSize(maxBinaryMessageSize);
        container.setMaxSessionIdleTimeout(maxSessionIdleTimeout);
        return container;
    }
}