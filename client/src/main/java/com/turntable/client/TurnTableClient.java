package com.turntable.client;

import com.turntable.client.interfaces.IChatClient;
import com.turntable.client.interfaces.IFriendClient;
import com.turntable.client.interfaces.IGameClient;
import com.turntable.client.interfaces.IUserClient;

public class TurnTableClient implements IUserClient, IGameClient, IFriendClient, IChatClient {

    public TurnTableClient() {}

    // --- IUserClient ---

    @Override
    public void createUser() {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public void getUser(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public void getAllUsers() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }

    @Override
    public void updateUser(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public void deleteUser(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    // --- IGameClient ---

    @Override
    public void createGame() {
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public void getGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public void getAllGames() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllGames'");
    }

    @Override
    public void updateGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'updateGame'");
    }

    @Override
    public void deleteGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteGame'");
    }

    // --- IFriendClient ---

    @Override
    public void addFriend(String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'addFriend'");
    }

    @Override
    public void getFriend(String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'getFriend'");
    }

    @Override
    public void getAllFriends() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllFriends'");
    }

    @Override
    public void updateFriend(String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'updateFriend'");
    }

    @Override
    public void removeFriend(String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'removeFriend'");
    }

    // --- IChatClient ---

    @Override
    public void createChat() {
        throw new UnsupportedOperationException("Unimplemented method 'createChat'");
    }

    @Override
    public void getChat(String chatId) {
        throw new UnsupportedOperationException("Unimplemented method 'getChat'");
    }

    @Override
    public void getAllChats() {
        throw new UnsupportedOperationException("Unimplemented method 'getAllChats'");
    }

    @Override
    public void updateChat(String chatId) {
        throw new UnsupportedOperationException("Unimplemented method 'updateChat'");
    }

    @Override
    public void deleteChat(String chatId) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteChat'");
    }
}
