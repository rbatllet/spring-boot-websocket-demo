package com.example.springbootwebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Test-specific WebSocket configuration that doesn't require a servlet container
 */
@Configuration
@EnableWebSocket
@Profile("test")
public class TestWebSocketConfig implements WebSocketConfigurer {

    @Autowired(required = false)
    private ChatMessageHandler chatMessageHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Solo registrar handlers si están disponibles
        if (chatMessageHandler != null) {
            registry.addHandler(chatMessageHandler, "/chat")
                    .setAllowedOrigins("*");
        }
    }
    
    /**
     * Mock para el contenedor de WebSocket que no requiere un servidor real
     * Este bean tiene prioridad sobre el definido en WebSocketConfig
     */
    @Bean
    @Primary
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        // Crear un mock del container que no causará errores en tests
        return null;
    }
}
