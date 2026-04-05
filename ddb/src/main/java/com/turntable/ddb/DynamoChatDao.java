package com.turntable.ddb;

import com.turntable.client.chat.ChatMessage;
import com.turntable.client.chat.IChatDao;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DynamoDB implementation of {@link IChatDao}.
 *
 * <p>Chat table schema:
 * <ul>
 *   <li>PK: {@code chatId}</li>
 *   <li>SK: {@code sk} ({@code messageTimestamp#messageId}) — ensures uniqueness and
 *       chronological ordering within a chat</li>
 *   <li>Attributes: {@code messageId}, {@code messageTimestamp}, {@code senderId},
 *       {@code content}</li>
 * </ul>
 */
@RequiredArgsConstructor
public class DynamoChatDao implements IChatDao {

    private static final String CHAT_ID = "chatId";
    private static final String SORT_KEY = "sk";
    private static final String MESSAGE_ID = "messageId";
    private static final String MESSAGE_TIMESTAMP = "messageTimestamp";
    private static final String SENDER_ID = "senderId";
    private static final String CONTENT = "content";

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    @Override
    public ChatMessage create(String chatId, ChatMessage message) {
        // Sort key is timestamp#messageId to preserve chronological order with uniqueness
        String sk = message.getMessageTimestamp().toString() + "#" + message.getMessageId();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put(CHAT_ID, AttributeValue.fromS(chatId));
        item.put(SORT_KEY, AttributeValue.fromS(sk));
        item.put(MESSAGE_ID, AttributeValue.fromS(message.getMessageId()));
        item.put(MESSAGE_TIMESTAMP, AttributeValue.fromS(message.getMessageTimestamp().toString()));
        item.put(SENDER_ID, AttributeValue.fromS(message.getSenderId()));
        item.put(CONTENT, AttributeValue.fromS(message.getContent()));

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());

        return message;
    }

    @Override
    public List<ChatMessage> findByChatId(String chatId) {
        QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#cid = :chatId")
                .expressionAttributeNames(Map.of("#cid", CHAT_ID))
                .expressionAttributeValues(Map.of(":chatId", AttributeValue.fromS(chatId)))
                .build());

        return response.items().stream()
                .map(this::itemToMessage)
                .collect(Collectors.toList());
    }

    private ChatMessage itemToMessage(Map<String, AttributeValue> item) {
        return ChatMessage.builder()
                .messageId(item.get(MESSAGE_ID).s())
                .messageTimestamp(Instant.parse(item.get(MESSAGE_TIMESTAMP).s()))
                .senderId(item.get(SENDER_ID).s())
                .content(item.get(CONTENT).s())
                .build();
    }
}
