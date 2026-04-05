package com.turntable.client.interfaces;

public interface IUserClient {
    // CreateUser
    // UpdateUser
    // RemoveUser? (How does this affect friends?)
    void createUser();
    void getUser(String userId);
    void getAllUsers();
    void updateUser(String userId);
    void deleteUser(String userId);
}
