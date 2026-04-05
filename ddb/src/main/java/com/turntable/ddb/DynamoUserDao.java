package com.turntable.ddb;

import com.turntable.client.user.IUserDao;
import com.turntable.client.user.User;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/** DynamoDB implementation of {@link IUserDao}. */
@RequiredArgsConstructor
public class DynamoUserDao implements IUserDao {

    private final DynamoDbClient dynamoDbClient;

    @Override
    public User create(User user) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public User findById(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void update(String userId, User user) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
