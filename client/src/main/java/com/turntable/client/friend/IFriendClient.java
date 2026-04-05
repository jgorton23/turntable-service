package com.turntable.client.friend;

import java.util.List;

public interface IFriendClient {
    List<Friend> listFriends(String userId, FriendStatus status);
    void sendFriendRequest(String fromUserId, String toUserId);
    void acceptFriendRequest(String fromUserId, String toUserId);
    void declineFriendRequest(String fromUserId, String toUserId);
    void removeFriend(String userId, String friendId);
}
