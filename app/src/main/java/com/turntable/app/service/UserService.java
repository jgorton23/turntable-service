package com.turntable.app.service;

import com.turntable.app.model.User;
import com.turntable.client.user.IUserClient;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService {

    private final IUserClient userClient;

    public User getUser(String userId) {
        com.turntable.client.user.User clientUser = userClient.getUser(userId);
        return clientUser == null ? null : toModel(clientUser);
    }

    public User createUser(User user) {
        return toModel(userClient.createUser(toClientModel(user)));
    }

    public void updateUser(String userId, User user) {
        userClient.updateUser(userId, toClientModel(user));
    }

    public void deleteUser(String userId) {
        userClient.deleteUser(userId);
    }

    private static User toModel(com.turntable.client.user.User u) {
        return User.builder()
                .userId(u.getUserId())
                .username(u.getUsername())
                .email(u.getEmail())
                .avatar(u.getAvatar())
                .build();
    }

    private static com.turntable.client.user.User toClientModel(User u) {
        return com.turntable.client.user.User.builder()
                .userId(u.getUserId())
                .username(u.getUsername())
                .email(u.getEmail())
                .avatar(u.getAvatar())
                .build();
    }
}
