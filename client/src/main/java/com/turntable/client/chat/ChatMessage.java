package com.turntable.client.chat;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatMessage {

    /** The unique identifier for this message. */
    private final String messageId;

    /** The ID of the chat this message belongs to. */
    private final String chatId;

    /** The ID of the user who sent this message. */
    private final String senderId;

    /** The content of the message. */
    private final String content;

    /** The timestamp when the message was sent. */
    private final Instant messageTimestamp;
}
