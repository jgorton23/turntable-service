package com.turntable.client.user;

/** Defines user management operations. */
public interface IUserClient {

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user
     */
    User createUser(User user);

    /**
     * Updates an existing user's details.
     *
     * @param userId the ID of the user to update
     * @param user   the updated user data
     */
    void updateUser(String userId, User user);

    /**
     * Deletes a user.
     *
     * @param userId the ID of the user to delete
     */
    void deleteUser(String userId);
}
