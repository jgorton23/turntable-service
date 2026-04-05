package com.turntable.client.user;

/** Defines user management operations. */
public interface IUserClient {

    /**
     * Creates a new user.
     */
    void createUser();

    /**
     * Updates an existing user's details.
     *
     * @param userId the ID of the user to update
     */
    void updateUser(String userId);

    /**
     * Deletes a user.
     *
     * @param userId the ID of the user to delete
     */
    void deleteUser(String userId);
}
