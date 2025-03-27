package com.example.springbootwebsocket.controller;

import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller to expose internationalized messages to the frontend
 */
@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageSource messageSource;
    
    public MessageController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    /**
     * Endpoint to get all messages for a specific language
     * 
     * @param lang The language code (default: en)
     * @return A map of all message keys and their translated values
     */
    @GetMapping("/messages")
    public Map<String, String> getMessages(@RequestParam(defaultValue = "en") String lang) {
        Map<String, String> messages = new HashMap<>();
        Locale locale = Locale.forLanguageTag(lang);
        
        // Log the requested locale
        System.out.println("Loading messages for locale: " + locale);
        
        // Define a list of message keys to export
        String[] messageKeys = {
            "app.title",
            "ui.language.selector",
            "ui.connection.disconnected",
            "ui.connection.connected",
            "ui.connection.connecting",
            "ui.error.name.required",
            "ui.error.message.required",
            "ui.error.connection.failed",
            "ui.error.websocket",
            "ui.error.send.failed",
            "ui.error.not.connected",
            "ui.error.display.message",
            "ui.button.connect",
            "ui.button.send",
            "ui.input.name.placeholder",
            "ui.input.message.placeholder",
            "chat.message.join",
            "chat.message.leave",
            "chat.message.error.processing",
            "chat.message.system",
            // Plural forms
            "users.online.zero",
            "users.online.one", 
            "users.online.other"
        };
        
        // Add each message to the map
        for (String key : messageKeys) {
            try {
                String message = messageSource.getMessage(key, null, locale);
                messages.put(key, message);
                
                // Log each message for debugging
                if (key.startsWith("users.online")) {
                    System.out.println("Loaded key: " + key + " = " + message);
                }
            } catch (Exception e) {
                System.err.println("Error loading message for key: " + key);
                e.printStackTrace();
                // Put the key itself as the value to make missing keys obvious
                messages.put(key, "[MISSING: " + key + "]");
            }
        }
        
        // Print a summary
        System.out.println("Total messages loaded: " + messages.size() + " for locale: " + locale);
        
        return messages;
    }
}
