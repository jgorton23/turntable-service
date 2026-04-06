package com.turntable.app.service;

import com.turntable.app.model.ChatMessage;
import com.turntable.client.chat.IChatClient;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ChatService {

    private final IChatClient chatClient;

    public void sendMessage(String chatId, ChatMessage message) {
        chatClient.sendMessage(chatId, toClientModel(message));
    }

    public List<ChatMessage> listMessages(String chatId) {
        return chatClient.listMessages(chatId)
                .stream()
                .map(ChatService::toModel)
                .collect(Collectors.toList());
    }

    private static ChatMessage toModel(com.turntable.client.chat.ChatMessage m) {
        return ChatMessage.builder()
                .messageId(m.getMessageId())
                .messageTimestamp(m.getMessageTimestamp())
                .senderId(m.getSenderId())
                .content(m.getContent())
                .build();
    }

    private static com.turntable.client.chat.ChatMessage toClientModel(ChatMessage m) {
        return com.turntable.client.chat.ChatMessage.builder()
                .messageId(m.getMessageId())
                .messageTimestamp(m.getMessageTimestamp())
                .senderId(m.getSenderId())
                .content(m.getContent())
                .build();
    }
}
