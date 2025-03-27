package com.example.springbootwebsocket;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Utility class for handling internationalized messages
 */
@Component
public class MessageUtils {

    private final MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Get a message in the current locale
     *
     * @param code The message code
     * @param args Optional arguments for message formatting
     * @return The localized message
     */
    public String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * Get a message in a specific locale
     *
     * @param code   The message code
     * @param locale The desired locale
     * @param args   Optional arguments for message formatting
     * @return The localized message
     */
    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
