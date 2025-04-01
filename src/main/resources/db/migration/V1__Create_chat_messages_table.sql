-- Create chat_messages table
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    timestamp VARCHAR(255) NOT NULL,
    message_type VARCHAR(50) NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_chat_messages_name ON chat_messages(name);
CREATE INDEX idx_chat_messages_message_type ON chat_messages(message_type);
CREATE INDEX idx_chat_messages_timestamp ON chat_messages(timestamp);
