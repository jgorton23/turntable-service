package com.turntable.client.friend;

import java.util.List;

/** Defines data access operations for friend relationships. */
public interface IFriendDao {

    /**
     * Retrieves all friends for a user, filtered by friendship status.
     *
     * @param userId the ID of the user
     * @param status the friendship status to filter by
     * @return the list of matching friends
     */
    List<Friend> findByUser(String userId, FriendStatus status);

    /**
     * Persists a new friend request between two users.
     *
     * @param fromUserId the ID of the user sending the request
     * @param toUserId   the ID of the user receiving the request
     */
    void create(String fromUserId, String toUserId);

    /**
     * Updates the status of a friendship.
     *
     * @param userId   the ID of the user
     * @param friendId the ID of the friend
     * @param status   the new friendship status
     */
    void updateStatus(String userId, String friendId, FriendStatus status);

    /**
     * Deletes a friendship between two users.
     *
     * @param userId   the ID of the user
     * @param friendId the ID of the friend to remove
     */
    void delete(String userId, String friendId);
}
