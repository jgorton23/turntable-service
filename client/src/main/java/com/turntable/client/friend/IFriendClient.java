package com.turntable.client.friend;

import java.util.List;

public interface IFriendClient {
    List<Friend> listFriends(String userId, FriendStatus status);
    void sendFriendRequest(String userId, String toUserId);
    void acceptFriendRequest(String userId, String requesterId);
    void declineFriendRequest(String userId, String requesterId);
    void removeFriend(String userId, String friendId);
}
