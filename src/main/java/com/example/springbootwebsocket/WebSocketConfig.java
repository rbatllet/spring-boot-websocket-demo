package com.example.springbootwebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Configuration class for WebSocket endpoints and settings
 */
@Configuration
@EnableWebSocket
@ConditionalOnWebApplication(type = Type.SERVLET)  // Solo se activa en aplicaciones web completas
@Profile("!test")  // No se activa en el perfil de test
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatMessageHandler chatMessageHandler;

    @Autowired
    public WebSocketConfig(ChatMessageHandler chatMessageHandler) {
        this.chatMessageHandler = chatMessageHandler;
    }

    /**
     * Registers WebSocket handlers with their URL paths
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatMessageHandler, "/chat")
                .setAllowedOrigins("*"); // For development - restrict in production
    }
    
    /**
     * Configures WebSocket message size limits and timeouts
     * to prevent abuse and ensure server stability
     *
     * @return The configured WebSocket container bean
     */
    @Bean
    @ConditionalOnMissingBean(name = "serverContainer")  // Para evitar crear si ya existe
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        
        // Set message size limits
        container.setMaxTextMessageBufferSize(8192); // 8KB
        container.setMaxBinaryMessageBufferSize(8192); // 8KB
        
        // Set timeouts
        container.setMaxSessionIdleTimeout(3600000L); // 1 hour in milliseconds
        container.setAsyncSendTimeout(30000L); // 30 seconds
        
        return container;
    }
}