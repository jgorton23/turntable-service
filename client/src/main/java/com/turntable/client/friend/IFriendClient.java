package com.turntable.client.friend;

import java.util.List;

public interface IFriendClient {

    /**
     * Lists friends for a user, filtered by friendship status.
     *
     * @param userId the ID of the user
     * @param status the friendship status to filter by (ACCEPTED, INVITATION_SENT, INVITATION_RECEIVED)
     * @return the list of matching friends
     */
    List<Friend> listFriends(String userId, FriendStatus status);

    /**
     * Sends a friend request from one user to another.
     *
     * @param userId   the ID of the user sending the request
     * @param toUserId the ID of the user receiving the request
     */
    void sendFriendRequest(String userId, String toUserId);

    /**
     * Accepts a pending friend request.
     *
     * @param userId      the ID of the user accepting the request
     * @param requesterId the ID of the user who sent the request
     */
    void acceptFriendRequest(String userId, String requesterId);

    /**
     * Declines a pending friend request.
     *
     * @param userId      the ID of the user declining the request
     * @param requesterId the ID of the user who sent the request
     */
    void declineFriendRequest(String userId, String requesterId);

    /**
     * Removes an existing friendship between two users.
     *
     * @param userId   the ID of the user removing the friend
     * @param friendId the ID of the friend to remove
     */
    void removeFriend(String userId, String friendId);
}
