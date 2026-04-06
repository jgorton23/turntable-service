package com.turntable.app.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/** API-level representation of a chat message. */
@Value
@Builder
public class ChatMessage {
    String messageId;
    Instant messageTimestamp;
    String senderId;
    String content;
}
