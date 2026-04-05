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

/** Default implementation of the TurnTable client interfaces. */
public class TurnTableClient implements IUserClient, IGameClient, IFriendClient, IChatClient {

    /** Creates a new TurnTableClient. */
    public TurnTableClient() {}

    // --- IUserClient ---

    /** {@inheritDoc} */
    @Override
    public void createUser() {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    /** {@inheritDoc} */
    @Override
    public void updateUser(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    /** {@inheritDoc} */
    @Override
    public void deleteUser(String userId) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    // --- IGameClient ---

    /** {@inheritDoc} */
    @Override
    public Game createGame(Game game) {
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    /** {@inheritDoc} */
    @Override
    public Game getGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'getGame'");
    }

    /** {@inheritDoc} */
    @Override
    public List<Game> listGames(String playerId, GameStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    /** {@inheritDoc} */
    @Override
    public void cancelGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'cancelGame'");
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String gameId, String playerId) {
        throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
    }

    /** {@inheritDoc} */
    @Override
    public void startGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'startGame'");
    }

    /** {@inheritDoc} */
    @Override
    public void endGame(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'endGame'");
    }

    /** {@inheritDoc} */
    @Override
    public List<Move> listMoves(String gameId) {
        throw new UnsupportedOperationException("Unimplemented method 'listMoves'");
    }

    /** {@inheritDoc} */
    @Override
    public void doMove(String gameId, String playerId, Move move) {
        throw new UnsupportedOperationException("Unimplemented method 'doMove'");
    }

    // --- IFriendClient ---

    /** {@inheritDoc} */
    @Override
    public List<Friend> listFriends(String userId, FriendStatus status) {
        throw new UnsupportedOperationException("Unimplemented method 'listFriends'");
    }

    /** {@inheritDoc} */
    @Override
    public void sendFriendRequest(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'sendFriendRequest'");
    }

    /** {@inheritDoc} */
    @Override
    public void acceptFriendRequest(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'acceptFriendRequest'");
    }

    /** {@inheritDoc} */
    @Override
    public void declineFriendRequest(String fromUserId, String toUserId) {
        throw new UnsupportedOperationException("Unimplemented method 'declineFriendRequest'");
    }

    /** {@inheritDoc} */
    @Override
    public void removeFriend(String userId, String friendId) {
        throw new UnsupportedOperationException("Unimplemented method 'removeFriend'");
    }

    // --- IChatClient ---

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String chatId, ChatMessage message) {
        throw new UnsupportedOperationException("Unimplemented method 'sendMessage'");
    }

    /** {@inheritDoc} */
    @Override
    public List<ChatMessage> listMessages(String chatId) {
        throw new UnsupportedOperationException("Unimplemented method 'listMessages'");
    }
}
