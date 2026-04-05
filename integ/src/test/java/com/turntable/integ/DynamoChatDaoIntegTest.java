package com.turntable.integ;

import com.turntable.client.chat.ChatMessage;
import com.turntable.ddb.DynamoChatDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamoChatDaoIntegTest extends DynamoDbIntegBase {

    private static final String TABLE = "integ-chat";
    private static DynamoChatDao dao;

    @BeforeAll
    static void setUpTable() {
        createTable(TABLE, "chatId", "sk");
        dao = new DynamoChatDao(CLIENT, TABLE);
    }

    private ChatMessage message(String id, Instant ts, String sender, String content) {
        return ChatMessage.builder()
                .messageId(id).messageTimestamp(ts)
                .senderId(sender).content(content)
                .build();
    }

    @Test
    void createAndFindByChatId_roundTrip() {
        Instant ts = Instant.parse("2025-06-01T12:00:00Z");
        ChatMessage msg = message("integ-m1", ts, "u1", "hello");

        dao.create("integ-chat1", msg);
        List<ChatMessage> messages = dao.findByChatId("integ-chat1");

        assertEquals(1, messages.size());
        ChatMessage found = messages.get(0);
        assertEquals("integ-m1", found.getMessageId());
        assertEquals(ts, found.getMessageTimestamp());
        assertEquals("u1", found.getSenderId());
        assertEquals("hello", found.getContent());
    }

    @Test
    void create_returnsInputMessage() {
        ChatMessage msg = message("integ-m2", Instant.now(), "u1", "hi");
        assertEquals(msg, dao.create("integ-chat2", msg));
    }

    @Test
    void findByChatId_returnsMessagesInChronologicalOrder() {
        Instant ts1 = Instant.parse("2025-06-01T09:00:00Z");
        Instant ts2 = Instant.parse("2025-06-01T10:00:00Z");
        Instant ts3 = Instant.parse("2025-06-01T11:00:00Z");

        // Insert out of order to confirm DynamoDB sorts by sk
        dao.create("integ-chat3", message("integ-m5", ts3, "u2", "third"));
        dao.create("integ-chat3", message("integ-m3", ts1, "u1", "first"));
        dao.create("integ-chat3", message("integ-m4", ts2, "u1", "second"));

        List<ChatMessage> messages = dao.findByChatId("integ-chat3");

        assertEquals(3, messages.size());
        assertEquals("first", messages.get(0).getContent());
        assertEquals("second", messages.get(1).getContent());
        assertEquals("third", messages.get(2).getContent());
    }

    @Test
    void findByChatId_returnsEmptyList_whenNoMessages() {
        assertTrue(dao.findByChatId("integ-chat-empty").isEmpty());
    }

    @Test
    void findByChatId_isolatesByChatId() {
        dao.create("integ-chat4", message("integ-m6", Instant.now(), "u1", "chat4 only"));
        dao.create("integ-chat5", message("integ-m7", Instant.now(), "u2", "chat5 only"));

        List<ChatMessage> chat4 = dao.findByChatId("integ-chat4");
        List<ChatMessage> chat5 = dao.findByChatId("integ-chat5");

        assertEquals(1, chat4.size());
        assertEquals("chat4 only", chat4.get(0).getContent());
        assertEquals(1, chat5.size());
        assertEquals("chat5 only", chat5.get(0).getContent());
    }
}
