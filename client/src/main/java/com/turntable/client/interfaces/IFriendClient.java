package com.turntable.client.interfaces;

public interface IFriendClient {
    // List friends for user (filter for sent invitations, received invitations, accepted)
    // Send friend request
    // Accept friend request
    // Decline friend request
    // Remove friend
    void addFriend(String friendId);
    void getFriend(String friendId);
    void getAllFriends();
    void updateFriend(String friendId);
    void removeFriend(String friendId);
}
