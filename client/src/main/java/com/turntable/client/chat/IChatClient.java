package com.turntable.client.chat;

import java.util.List;

/** Defines chat operations. */
public interface IChatClient {

    /**
     * Sends a message to a chat.
     *
     * @param chatId  the ID of the chat
     * @param message the message to send
     */
    void sendMessage(String chatId, ChatMessage message);

    /**
     * Lists all messages in a chat.
     *
     * @param chatId the ID of the chat
     * @return the ordered list of messages
     */
    List<ChatMessage> listMessages(String chatId);
}
