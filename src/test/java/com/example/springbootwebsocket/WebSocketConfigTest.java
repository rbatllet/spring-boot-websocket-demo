package com.example.springbootwebsocket;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class WebSocketConfigTest {

    @Test
    void testRegisterWebSocketHandlers() {
        // Create mocks
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);
        ChatMessageHandler chatMessageHandler = mock(ChatMessageHandler.class);
        
        // Configure mocks
        when(registry.addHandler(any(), anyString())).thenReturn(registration);
        when(registration.setAllowedOrigins(any(String.class))).thenReturn(registration);
        
        // Create the config with the mocked handler
        WebSocketConfig config = new WebSocketConfig(chatMessageHandler);
        
        // Set the externalized properties using reflection
        ReflectionTestUtils.setField(config, "endpoint", "/ws/chat");
        ReflectionTestUtils.setField(config, "allowedOrigins", "http://localhost:8080");
        
        // Call the method to test
        config.registerWebSocketHandlers(registry);
        
        // Verify the handler was registered with the correct path
        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        verify(registry).addHandler(eq(chatMessageHandler), pathCaptor.capture());
        assertEquals("/ws/chat", pathCaptor.getValue());
        
        // Verify CORS was configured with the correct origins
        ArgumentCaptor<String> originsCaptor = ArgumentCaptor.forClass(String.class);
        verify(registration).setAllowedOrigins(originsCaptor.capture());
        assertEquals("http://localhost:8080", originsCaptor.getValue());
    }
}
