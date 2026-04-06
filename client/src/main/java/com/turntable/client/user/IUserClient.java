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
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return the user, or null if not found
     */
    User getUser(String userId);

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
