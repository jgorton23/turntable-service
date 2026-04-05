package com.turntable.client;

import com.turntable.client.chat.ChatMessage;
import com.turntable.client.chat.IChatClient;
import com.turntable.client.friend.Friend;
import com.turntable.client.friend.FriendStatus;
import com.turntable.client.friend.IFriendClient;
import com.turntable.client.game.Game;
import com.turntable.client.game.GameStatus;
import com.turntable.client.game.IGameClient;
import com.turntable.client.game.Move;
import com.turntable.client.user.IUserClient;

import java.util.List;

public class TurnTableClient implements IUserClient, IGameClient, IFriendClient, IChatClient {

    public TurnTableClient() {}

    // --- IUserClient ---

    @Override
    public void createUser() {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
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
    public Game createGame(Game game) {
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public Game getGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    @Override
    public List<Game> listGames(String playerId, GameStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public void cancelGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'cancelGame'");
    }

    @Override
    public void joinGame(String gameId, String playerId) {
        throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
    }

    @Override
    public void startGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'startGame'");
    }

    @Override
    public void endGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'endGame'");
    }

    @Override
    public List<Move> listMoves(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'listMoves'");
    }

    @Override
    public void doMove(String gameId, String playerId, Move move) {
        throw new UnsupportedOperationException("Unimplemented method 'doMove'");
    }

    // --- IFriendClient ---

    @Override
    public List<Friend> listFriends(String userId, FriendStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'listFriends'");
    }

    @Override
    public void sendFriendRequest(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'sendFriendRequest'");
    }

    @Override
    public void acceptFriendRequest(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'acceptFriendRequest'");
    }

    @Override
    public void declineFriendRequest(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'declineFriendRequest'");
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'removeFriend'");
    }

    // --- IChatClient ---

    @Override
    public void sendMessage(String chatId, ChatMessage message) {
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }

    @Override
    public List<ChatMessage> listMessages(String chatId) {
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }
}
