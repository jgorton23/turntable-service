package com.turntable.client.user;

public interface IUserClient {
    void createUser();
    void updateUser(String userId);
    void deleteUser(String userId);
}
