-- Add count column to chat_messages table
ALTER TABLE chat_messages ADD COLUMN count INT DEFAULT 0;
