package com.turntable.client.chat;

import java.util.List;

public interface IChatClient {
    void sendMessage(String chatId, ChatMessage message);
    List<ChatMessage> listMessages(String chatId);
}
