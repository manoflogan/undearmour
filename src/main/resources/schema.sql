DROP TABLE IF EXISTS "Chats";
CREATE TABLE IF NOT EXISTS "Chats" ( `username` TEXT, `chat_text` TEXT, `chat_id` INTEGER, `expiration_date` TEXT, PRIMARY KEY(`chat_id`) )