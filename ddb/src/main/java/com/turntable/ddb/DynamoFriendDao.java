package com.turntable.ddb;

import com.turntable.client.friend.Friend;
import com.turntable.client.friend.FriendStatus;
import com.turntable.client.friend.IFriendDao;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DynamoDB implementation of {@link IFriendDao}.
 *
 * <p>Friends table schema:
 * <ul>
 *   <li>PK: {@code userId} — the user who owns this view of the relationship</li>
 *   <li>SK: {@code friendId} — the friend's user ID</li>
 *   <li>Attributes: {@code friendUsername}, {@code status}, {@code friendsSince} (optional)</li>
 * </ul>
 *
 * <p>Each friendship is stored as two items — one from each user's perspective —
 * so that queries on {@code userId} return the full friend list for that user.
 */
@RequiredArgsConstructor
public class DynamoFriendDao implements IFriendDao {

    private static final String USER_ID = "userId";
    private static final String FRIEND_ID = "friendId";
    private static final String FRIEND_USERNAME = "friendUsername";
    private static final String STATUS = "status";
    private static final String FRIENDS_SINCE = "friendsSince";
    private static final String USERNAME = "username";

    private final DynamoDbClient dynamoDbClient;
    private final String friendsTableName;
    private final String usersTableName;

    @Override
    public List<Friend> findByUser(String userId, FriendStatus status) {
        QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
                .tableName(friendsTableName)
                .keyConditionExpression("#uid = :userId")
                .filterExpression("#s = :status")
                .expressionAttributeNames(Map.of(
                        "#uid", USER_ID,
                        "#s", STATUS
                ))
                .expressionAttributeValues(Map.of(
                        ":userId", AttributeValue.fromS(userId),
                        ":status", AttributeValue.fromS(status.name())
                ))
                .build());

        return response.items().stream()
                .map(this::itemToFriend)
                .collect(Collectors.toList());
    }

    @Override
    public void create(String fromUserId, String toUserId) {
        String fromUsername = getUsername(fromUserId);
        String toUsername = getUsername(toUserId);

        // fromUser sees INVITATION_SENT; toUser sees INVITATION_RECEIVED
        putFriendItem(fromUserId, toUserId, toUsername, FriendStatus.INVITATION_SENT, null);
        putFriendItem(toUserId, fromUserId, fromUsername, FriendStatus.INVITATION_RECEIVED, null);
    }

    @Override
    public void updateStatus(String userId, String friendId, FriendStatus status) {
        StringBuilder updateExpr = new StringBuilder("SET #s = :status");
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":status", AttributeValue.fromS(status.name()));

        if (status == FriendStatus.ACCEPTED) {
            updateExpr.append(", ").append(FRIENDS_SINCE).append(" = :friendsSince");
            valueMap.put(":friendsSince", AttributeValue.fromS(Instant.now().toString()));
        }

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(friendsTableName)
                .key(Map.of(
                        USER_ID, AttributeValue.fromS(userId),
                        FRIEND_ID, AttributeValue.fromS(friendId)
                ))
                .updateExpression(updateExpr.toString())
                .expressionAttributeNames(Map.of("#s", STATUS))
                .expressionAttributeValues(valueMap)
                .build());
    }

    @Override
    public void delete(String userId, String friendId) {
        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(friendsTableName)
                .key(Map.of(
                        USER_ID, AttributeValue.fromS(userId),
                        FRIEND_ID, AttributeValue.fromS(friendId)
                ))
                .build());

        dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(friendsTableName)
                .key(Map.of(
                        USER_ID, AttributeValue.fromS(friendId),
                        FRIEND_ID, AttributeValue.fromS(userId)
                ))
                .build());
    }

    private String getUsername(String userId) {
        GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(usersTableName)
                .key(Map.of(USER_ID, AttributeValue.fromS(userId)))
                .projectionExpression(USERNAME)
                .build());
        return response.item().get(USERNAME).s();
    }

    private void putFriendItem(String userId, String friendId, String friendUsername,
                               FriendStatus status, Instant friendsSince) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ID, AttributeValue.fromS(userId));
        item.put(FRIEND_ID, AttributeValue.fromS(friendId));
        item.put(FRIEND_USERNAME, AttributeValue.fromS(friendUsername));
        item.put(STATUS, AttributeValue.fromS(status.name()));
        if (friendsSince != null) {
            item.put(FRIENDS_SINCE, AttributeValue.fromS(friendsSince.toString()));
        }

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(friendsTableName)
                .item(item)
                .build());
    }

    private Friend itemToFriend(Map<String, AttributeValue> item) {
        return Friend.builder()
                .username(item.get(FRIEND_USERNAME).s())
                .status(FriendStatus.valueOf(item.get(STATUS).s()))
                .friendsSince(item.containsKey(FRIENDS_SINCE)
                        ? Instant.parse(item.get(FRIENDS_SINCE).s())
                        : null)
                .build();
    }
}
