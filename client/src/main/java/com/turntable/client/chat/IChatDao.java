package com.turntable.client.chat;

import java.util.List;

/** Defines data access operations for chat messages. */
public interface IChatDao {

    /**
     * Persists a new message to a chat.
     *
     * @param chatId  the ID of the chat
     * @param message the message to persist
     * @return the created message
     */
    ChatMessage create(String chatId, ChatMessage message);

    /**
     * Retrieves all messages in a chat.
     *
     * @param chatId the ID of the chat
     * @return the ordered list of messages
     */
    List<ChatMessage> findByChatId(String chatId);
}
