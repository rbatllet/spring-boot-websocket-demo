package com.example.springbootwebsocket.security;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utility class for validating and sanitizing chat messages to prevent XSS and injection attacks
 */
@Component
public class MessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(MessageValidator.class);
    
    // Maximum allowed message length
    private static final int MAX_MESSAGE_LENGTH = 1000;
    
    // Pattern for detecting potentially malicious content
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script(.*?)>(.*?)</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_HANDLER_PATTERN = Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATA_URI_PATTERN = Pattern.compile("data:\\s*[^;]*;base64", Pattern.CASE_INSENSITIVE);
    
    // OWASP HTML Sanitizer policy
    private final PolicyFactory policy;
    
    public MessageValidator() {
        // Configure HTML sanitizer policy to only allow safe tags and attributes
        policy = new HtmlPolicyBuilder()
                .allowElements("b", "i", "u", "strong", "em", "mark", "small", "del", "ins", "sub", "sup")
                .allowUrlProtocols("https")
                .allowAttributes("class").onElements("b", "i", "u", "strong", "em", "mark", "small", "del", "ins", "sub", "sup")
                .toFactory();
    }
    
    /**
     * Validates a message for security issues
     * 
     * @param message The message to validate
     * @return true if the message is valid, false otherwise
     */
    public boolean isValid(String message) {
        if (message == null) {
            return false;
        }
        
        // Check message length
        if (message.length() > MAX_MESSAGE_LENGTH) {
            logger.warn("Message exceeds maximum length: {} characters", message.length());
            return false;
        }
        
        // Check for potentially malicious patterns
        if (SCRIPT_PATTERN.matcher(message).find() ||
            EVENT_HANDLER_PATTERN.matcher(message).find() ||
            DATA_URI_PATTERN.matcher(message).find()) {
            logger.warn("Potentially malicious content detected in message");
            return false;
        }
        
        return true;
    }
    
    /**
     * Sanitizes a message to prevent XSS attacks
     * 
     * @param message The message to sanitize
     * @return The sanitized message
     */
    public String sanitize(String message) {
        if (message == null) {
            return "";
        }
        
        // Apply HTML sanitization policy directly without escaping first
        // This allows safe HTML tags while removing unsafe ones
        String sanitized = policy.sanitize(message);
        
        return sanitized;
    }
    
    /**
     * Validates and sanitizes a message in one step
     * 
     * @param message The message to process
     * @return The sanitized message if valid, null if invalid
     */
    public String validateAndSanitize(String message) {
        if (isValid(message)) {
            return sanitize(message);
        }
        return null;
    }
}
