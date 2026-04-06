package com.turntable.app.service;

import com.turntable.app.model.Friend;
import com.turntable.app.model.FriendStatus;
import com.turntable.client.friend.IFriendClient;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class FriendService {

    private final IFriendClient friendClient;
    private final UserService userService;

    public List<Friend> listFriends(String userId, FriendStatus status) {
        return friendClient.listFriends(userId, toClientStatus(status))
                .stream()
                .map(f -> toModel(f, resolveUsername(f.getFriendId())))
                .collect(Collectors.toList());
    }

    public void sendFriendRequest(String userId, String toUserId) {
        friendClient.sendFriendRequest(userId, toUserId);
    }

    public void acceptFriendRequest(String userId, String requesterId) {
        friendClient.acceptFriendRequest(userId, requesterId);
    }

    public void declineFriendRequest(String userId, String requesterId) {
        friendClient.declineFriendRequest(userId, requesterId);
    }

    public void removeFriend(String userId, String friendId) {
        friendClient.removeFriend(userId, friendId);
    }

    private String resolveUsername(String friendId) {
        com.turntable.app.model.User user = userService.getUser(friendId);
        return user != null ? user.getUsername() : null;
    }

    private static Friend toModel(com.turntable.client.friend.Friend f, String friendUsername) {
        return Friend.builder()
                .friendId(f.getFriendId())
                .friendUsername(friendUsername)
                .status(toModelStatus(f.getStatus()))
                .friendsSince(f.getFriendsSince())
                .build();
    }

    private static FriendStatus toModelStatus(com.turntable.client.friend.FriendStatus status) {
        return FriendStatus.valueOf(status.name());
    }

    private static com.turntable.client.friend.FriendStatus toClientStatus(FriendStatus status) {
        return com.turntable.client.friend.FriendStatus.valueOf(status.name());
    }
}
