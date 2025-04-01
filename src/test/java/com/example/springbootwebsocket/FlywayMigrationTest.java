package com.example.springbootwebsocket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class FlywayMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFlywayMigrationApplied() {
        // Verify that the chat_messages table exists (created by Flyway)
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_NAME = 'CHAT_MESSAGES'");
        
        assertEquals(1, tables.size(), "The chat_messages table should exist");
    }

    @Test
    public void testChatMessagesTableStructure() {
        // Verify that the chat_messages table has the correct structure
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = 'CHAT_MESSAGES'");
        
        assertEquals(6, columns.size(), "The chat_messages table should have 6 columns");
        
        // Verify that the expected columns exist
        assertTrue(columns.stream().anyMatch(col -> "ID".equals(col.get("COLUMN_NAME"))),
                "The ID column should exist");
        assertTrue(columns.stream().anyMatch(col -> "NAME".equals(col.get("COLUMN_NAME"))),
                "The NAME column should exist");
        assertTrue(columns.stream().anyMatch(col -> "MESSAGE".equals(col.get("COLUMN_NAME"))),
                "The MESSAGE column should exist");
        assertTrue(columns.stream().anyMatch(col -> "TIMESTAMP".equals(col.get("COLUMN_NAME"))),
                "The TIMESTAMP column should exist");
        assertTrue(columns.stream().anyMatch(col -> "MESSAGE_TYPE".equals(col.get("COLUMN_NAME"))),
                "The MESSAGE_TYPE column should exist");
        assertTrue(columns.stream().anyMatch(col -> "COUNT".equals(col.get("COLUMN_NAME"))),
                "The COUNT column should exist");
    }

    @Test
    public void testChatMessagesTableIndexes() {
        // Verify that at least the primary key index exists
        List<Map<String, Object>> indexes = jdbcTemplate.queryForList(
                "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.INDEXES " +
                "WHERE TABLE_NAME = 'CHAT_MESSAGES'");
        
        assertTrue(indexes.size() >= 1, "The chat_messages table should have at least one index (primary key)");
        
        // Verify that the primary key exists
        boolean hasPrimaryKey = indexes.stream()
                .anyMatch(idx -> "PRIMARY_KEY_8".equals(idx.get("INDEX_NAME")) || 
                               idx.get("INDEX_NAME").toString().startsWith("PRIMARY"));
        
        assertTrue(hasPrimaryKey, "The chat_messages table should have a primary key");
    }
}
