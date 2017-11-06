DROP TABLE IF EXISTS Chats;CREATE TABLE IF NOT EXISTS Chats(username VARCHAR(20), chat_text VARCHAR(256), chat_id BIGINT, expiration_date TIMESTAMP, PRIMARY KEY(chat_id)); CREATE INDEX IF NOT EXISTS username_index on Chats(username); CREATE INDEX IF NOT EXISTS chat_id_index on Chats(chat_id); CREATE INDEX IF NOT EXISTS expiration_date_index on Chats(expiration_date);