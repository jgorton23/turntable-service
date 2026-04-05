package com.turntable.client.user;

/** Defines data access operations for users. */
public interface IUserDao {

    /**
     * Persists a new user.
     *
     * @param user the user to create
     * @return the created user
     */
    User create(User user);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user
     * @return the user, or null if not found
     */
    User findById(String userId);

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update
     * @param user   the updated user data
     */
    void update(String userId, User user);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    void delete(String userId);
}
