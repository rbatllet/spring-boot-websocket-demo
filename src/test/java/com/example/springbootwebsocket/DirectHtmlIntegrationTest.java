package com.example.springbootwebsocket;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test that bypasses Spring context initialization and directly tests the HTML file
 * This avoids issues with WebSocket configuration in tests
 */
public class DirectHtmlIntegrationTest {

    @Test
    void testIndexHtmlContainsI18nAttributes() throws IOException {
        // Get the path to the HTML file
        String basePath = System.getProperty("user.dir");
        Path htmlPath = Paths.get(basePath, "src", "main", "resources", "static", "index.html");
        File htmlFile = htmlPath.toFile();
        
        // Read the file content
        String content;
        try (BufferedReader reader = new BufferedReader(new FileReader(htmlFile))) {
            content = reader.lines().collect(Collectors.joining("\n"));
        }
        
        // Verify i18n attributes
        assertTrue(content.contains("data-i18n=\"app.title\""), "HTML should contain app.title attribute");
        assertTrue(content.contains("data-i18n=\"ui.button.connect\""), "HTML should contain connect button attribute");
        assertTrue(content.contains("data-i18n=\"ui.button.send\""), "HTML should contain send button attribute");
        assertTrue(content.contains("data-i18n-placeholder=\"ui.input.name.placeholder\""), "HTML should contain name placeholder attribute");
        
        // Verify JS files
        assertTrue(content.contains("src=\"js/i18n.js\""), "HTML should reference i18n.js");
        assertTrue(content.contains("src=\"js/chat.js\""), "HTML should reference chat.js");
    }
}
