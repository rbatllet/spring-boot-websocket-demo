package com.example.springbootwebsocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageUtilsTest {

    @Mock
    private MessageSource messageSource;

    private MessageUtils messageUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageUtils = new MessageUtils(messageSource);
        
        // Set up mock responses
        when(messageSource.getMessage(eq("test.key"), any(), any(Locale.class)))
                .thenReturn("Test message");
        when(messageSource.getMessage(eq("test.key.with.args"), any(), eq(Locale.ENGLISH)))
                .thenReturn("Test message with arg: test");
    }

    @Test
    void testGetMessageWithDefaultLocale() {
        // Set default locale
        Locale defaultLocale = Locale.getDefault();
        LocaleContextHolder.setLocale(defaultLocale);
        
        // Test getting a message
        String message = messageUtils.getMessage("test.key");
        
        // Verify the result and that the message source was called
        assertEquals("Test message", message);
        verify(messageSource).getMessage(eq("test.key"), any(), eq(defaultLocale));
    }

    @Test
    void testGetMessageWithSpecificLocale() {
        // Test getting a message with specific locale
        String message = messageUtils.getMessage("test.key.with.args", Locale.ENGLISH);
        
        // Verify the result and that the message source was called
        assertEquals("Test message with arg: test", message);
        verify(messageSource).getMessage(eq("test.key.with.args"), any(), eq(Locale.ENGLISH));
    }

    @Test
    void testGetMessageWithArguments() {
        // Set up mock for arguments
        when(messageSource.getMessage(eq("test.with.args"), eq(new Object[]{"value"}), any(Locale.class)))
                .thenReturn("Message with value");
        
        // Test getting a message with arguments
        String message = messageUtils.getMessage("test.with.args", "value");
        
        // Verify the result
        assertEquals("Message with value", message);
    }
}
