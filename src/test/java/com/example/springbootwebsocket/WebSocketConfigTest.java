package com.example.springbootwebsocket;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    @Test
    void testRegisterWebSocketHandlers() {
        // Create mocks
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);
        ChatMessageHandler chatMessageHandler = mock(ChatMessageHandler.class);
        
        // Configure mocks
        when(registry.addHandler(any(), anyString())).thenReturn(registration);
        when(registration.setAllowedOrigins(anyString())).thenReturn(registration);
        
        // Create the config with the mocked handler
        WebSocketConfig config = new WebSocketConfig(chatMessageHandler);
        
        // Call the method to test
        config.registerWebSocketHandlers(registry);
        
        // Verify the handler was registered with the correct path
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(registry).addHandler(eq(chatMessageHandler), pathCaptor.capture());
        assertEquals("/chat", pathCaptor.getValue());
        
        // Verify CORS was configured
        verify(registration).setAllowedOrigins("*");
    }
}
