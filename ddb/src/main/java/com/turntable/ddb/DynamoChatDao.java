package com.turntable.ddb;

import com.turntable.client.chat.ChatMessage;
import com.turntable.client.chat.IChatDao;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

/** DynamoDB implementation of {@link IChatDao}. */
@RequiredArgsConstructor
public class DynamoChatDao implements IChatDao {

    private final DynamoDbClient dynamoDbClient;

    @Override
    public ChatMessage create(String chatId, ChatMessage message) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public List<ChatMessage> findByChatId(String chatId) {
        throw new UnsupportedOperationException("Unimplemented method 'findByChatId'");
    }
}
