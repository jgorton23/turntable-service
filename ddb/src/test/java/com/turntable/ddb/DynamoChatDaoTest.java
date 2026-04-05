package com.turntable.ddb;

import com.turntable.client.chat.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoChatDaoTest {

    private static final String TABLE = "chat";

    private DynamoDbClient client;
    private DynamoChatDao dao;

    @BeforeEach
    void setUp() {
        client = mock(DynamoDbClient.class);
        dao = new DynamoChatDao(client, TABLE);
    }

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    void create_putsItemWithAllFields() {
        Instant ts = Instant.parse("2025-06-01T12:00:00Z");
        ChatMessage message = ChatMessage.builder()
                .messageId("m1").messageTimestamp(ts)
                .senderId("u1").content("hello")
                .build();

        dao.create("chat1", message);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        Map<String, AttributeValue> item = captor.getValue().item();

        assertEquals(TABLE, captor.getValue().tableName());
        assertEquals("chat1", item.get("chatId").s());
        assertEquals("m1", item.get("messageId").s());
        assertEquals(ts.toString(), item.get("messageTimestamp").s());
        assertEquals("u1", item.get("senderId").s());
        assertEquals("hello", item.get("content").s());
    }

    @Test
    void create_sortKeyIsTimestampHashMessageId() {
        Instant ts = Instant.parse("2025-06-01T12:00:00Z");
        ChatMessage message = ChatMessage.builder()
                .messageId("m1").messageTimestamp(ts)
                .senderId("u1").content("hello")
                .build();

        dao.create("chat1", message);

        ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);
        verify(client).putItem(captor.capture());
        assertEquals(ts + "#m1", captor.getValue().item().get("sk").s());
    }

    @Test
    void create_returnsInputMessage() {
        ChatMessage message = ChatMessage.builder()
                .messageId("m1").messageTimestamp(Instant.now())
                .senderId("u1").content("hello")
                .build();

        assertEquals(message, dao.create("chat1", message));
    }

    // ── findByChatId ──────────────────────────────────────────────────────────

    @Test
    void findByChatId_queriesWithChatId() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of()).build());

        dao.findByChatId("chat1");

        ArgumentCaptor<QueryRequest> captor = ArgumentCaptor.forClass(QueryRequest.class);
        verify(client).query(captor.capture());
        QueryRequest req = captor.getValue();

        assertEquals(TABLE, req.tableName());
        assertEquals("chat1", req.expressionAttributeValues().get(":chatId").s());
    }

    @Test
    void findByChatId_returnsMappedMessages() {
        Instant ts = Instant.parse("2025-06-01T12:00:00Z");
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of(Map.of(
                        "messageId", AttributeValue.fromS("m1"),
                        "messageTimestamp", AttributeValue.fromS(ts.toString()),
                        "senderId", AttributeValue.fromS("u1"),
                        "content", AttributeValue.fromS("hello")
                ))).build());

        List<ChatMessage> messages = dao.findByChatId("chat1");

        assertEquals(1, messages.size());
        ChatMessage m = messages.get(0);
        assertEquals("m1", m.getMessageId());
        assertEquals(ts, m.getMessageTimestamp());
        assertEquals("u1", m.getSenderId());
        assertEquals("hello", m.getContent());
    }

    @Test
    void findByChatId_returnsEmptyList_whenNoneFound() {
        when(client.query(any(QueryRequest.class))).thenReturn(
                QueryResponse.builder().items(List.of()).build());

        assertEquals(List.of(), dao.findByChatId("chat1"));
    }
}
