package com.turntable.ddb;

import com.turntable.client.friend.Friend;
import com.turntable.client.friend.FriendStatus;
import com.turntable.client.friend.IFriendDao;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

/** DynamoDB implementation of {@link IFriendDao}. */
@RequiredArgsConstructor
public class DynamoFriendDao implements IFriendDao {

    private final DynamoDbClient dynamoDbClient;

    @Override
    public List<Friend> findByUser(String userId, FriendStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'findByUser'");
    }

    @Override
    public void create(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void updateStatus(String userId, String friendId, FriendStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'updateStatus'");
    }

    @Override
    public void delete(String userId, String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
