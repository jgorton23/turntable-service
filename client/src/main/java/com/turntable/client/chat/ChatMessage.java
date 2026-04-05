package com.turntable.client.chat;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatMessage {
    private final String messageId;
    private final String chatId;
    private final String senderId;
    private final String content;
    private final Instant messageTimestamp;
}
