package com.turntable.ddb;

import com.turntable.client.user.IUserDao;
import com.turntable.client.user.User;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** DynamoDB implementation of {@link IUserDao}. */
@RequiredArgsConstructor
public class DynamoUserDao implements IUserDao {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String AVATAR = "avatar";

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    @Override
    public User create(User user) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ID, AttributeValue.fromS(user.getUserId()));
        item.put(USERNAME, AttributeValue.fromS(user.getUsername()));
        if (user.getEmail() != null) {
            item.put(EMAIL, AttributeValue.fromS(user.getEmail()));
        }
        if (user.getAvatar() != null) {
            item.put(AVATAR, AttributeValue.fromS(user.getAvatar()));
        }

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());

        return user;
    }

    @Override
    public User findById(String userId) {
        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(USER_ID, AttributeValue.fromS(userId)))
                .build());

        if (!response.hasItem() || response.item().isEmpty()) {
            return null;
        }

        return itemToUser(response.item());
    }

    @Override
    public void update(String userId, User user) {
        StringBuilder setExpr = new StringBuilder("SET #un = :username");
        List<String> removeAttrs = new ArrayList<>();
        Map<String, String> nameMap = new HashMap<>();
        Map<String, AttributeValue> valueMap = new HashMap<>();

        nameMap.put("#un", USERNAME);
        valueMap.put(":username", AttributeValue.fromS(user.getUsername()));

        if (user.getEmail() != null) {
            setExpr.append(", #em = :email");
            nameMap.put("#em", EMAIL);
            valueMap.put(":email", AttributeValue.fromS(user.getEmail()));
        } else {
            nameMap.put("#em", EMAIL);
            removeAttrs.add("#em");
        }

        if (user.getAvatar() != null) {
            setExpr.append(", #av = :avatar");
            nameMap.put("#av", AVATAR);
            valueMap.put(":avatar", AttributeValue.fromS(user.getAvatar()));
        } else {
            nameMap.put("#av", AVATAR);
            removeAttrs.add("#av");
        }

        String updateExpr = setExpr.toString();
        if (!removeAttrs.isEmpty()) {
            updateExpr += " REMOVE " + String.join(", ", removeAttrs);
        }

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(USER_ID, AttributeValue.fromS(userId)))
                .updateExpression(updateExpr)
                .expressionAttributeNames(nameMap)
                .expressionAttributeValues(valueMap)
                .build());
    }

    @Override
    public void delete(String userId) {
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(USER_ID, AttributeValue.fromS(userId)))
                .build());
    }

    private User itemToUser(Map<String, AttributeValue> item) {
        return User.builder()
                .userId(item.get(USER_ID).s())
                .username(item.get(USERNAME).s())
                .email(item.containsKey(EMAIL) ? item.get(EMAIL).s() : null)
                .avatar(item.containsKey(AVATAR) ? item.get(AVATAR).s() : null)
                .build();
    }
}
