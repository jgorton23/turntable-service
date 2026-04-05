package com.turntable.client;

import com.turntable.client.chat.ChatMessage;
import com.turntable.client.chat.IChatClient;
import com.turntable.client.chat.IChatDao;
import com.turntable.client.friend.Friend;
import com.turntable.client.friend.FriendStatus;
import com.turntable.client.friend.IFriendClient;
import com.turntable.client.friend.IFriendDao;
import com.turntable.client.game.Game;
import com.turntable.client.game.GameResult;
import com.turntable.client.game.GameStatus;
import com.turntable.client.game.IGameClient;
import com.turntable.client.game.IGameDao;
import com.turntable.client.game.Move;
import com.turntable.client.user.IUserClient;
import com.turntable.client.user.IUserDao;
import com.turntable.client.user.User;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/** Default implementation of the TurnTable client interfaces. */
@Builder
public class TurnTableClient implements IUserClient, IGameClient, IFriendClient, IChatClient {

    /** Data access for user operations. */
    @NonNull
    private final IUserDao userDao;

    /** Data access for game operations. */
    @NonNull
    private final IGameDao gameDao;

    /** Data access for friend operations. */
    @NonNull
    private final IFriendDao friendDao;

    /** Data access for chat operations. */
    @NonNull
    private final IChatDao chatDao;

    // --- IUserClient ---

    /** {@inheritDoc} */
    @Override
    public User createUser(User user) {
        return userDao.create(user);
    }

    /** {@inheritDoc} */
    @Override
    public void updateUser(String userId, User user) {
        userDao.update(userId, user);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteUser(String userId) {
        userDao.delete(userId);
    }

    // --- IGameClient ---

    /** {@inheritDoc} */
    @Override
    public Game createGame(Game game) {
        return gameDao.create(game);
    }

    /** {@inheritDoc} */
    @Override
    public Game getGame(String gameId) {
        return gameDao.findById(gameId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Game> listGames(String playerId, GameStatus status) {
        return gameDao.findByPlayer(playerId, status);
    }

    /** {@inheritDoc} */
    @Override
    public void cancelGame(String gameId) {
        Game game = gameDao.findById(gameId);
        gameDao.update(gameId, game.toBuilder().status(GameStatus.CANCELLED).build());
    }

    /** {@inheritDoc} */
    @Override
    public void joinGame(String gameId, String playerId) {
        Game game = gameDao.findById(gameId);
        List<String> players = new ArrayList<>(game.getPlayers());
        players.add(playerId);
        gameDao.update(gameId, game.toBuilder().players(players).build());
    }

    /** {@inheritDoc} */
    @Override
    public void startGame(String gameId) {
        Game game = gameDao.findById(gameId);
        gameDao.update(gameId, game.toBuilder().status(GameStatus.STARTED).build());
    }

    /** {@inheritDoc} */
    @Override
    public void endGame(String gameId, GameResult result) {
        Game game = gameDao.findById(gameId);
        gameDao.update(gameId, game.toBuilder().status(GameStatus.ENDED).result(result).build());
    }

    /** {@inheritDoc} */
    @Override
    public List<Move> listMoves(String gameId) {
        return gameDao.findMoves(gameId);
    }

    /** {@inheritDoc} */
    @Override
    public void doMove(String gameId, String playerId, Move move) {
        gameDao.createMove(gameId, move);
    }

    // --- IFriendClient ---

    /** {@inheritDoc} */
    @Override
    public List<Friend> listFriends(String userId, FriendStatus status) {
        return friendDao.findByUser(userId, status);
    }

    /** {@inheritDoc} */
    @Override
    public void sendFriendRequest(String userId, String toUserId) {
        friendDao.create(userId, toUserId);
    }

    /** {@inheritDoc} */
    @Override
    public void acceptFriendRequest(String userId, String requesterId) {
        friendDao.updateStatus(userId, requesterId, FriendStatus.ACCEPTED);
    }

    /** {@inheritDoc} */
    @Override
    public void declineFriendRequest(String userId, String requesterId) {
        friendDao.delete(userId, requesterId);
    }

    /** {@inheritDoc} */
    @Override
    public void removeFriend(String userId, String friendId) {
        friendDao.delete(userId, friendId);
    }

    // --- IChatClient ---

    /** {@inheritDoc} */
    @Override
    public void sendMessage(String chatId, ChatMessage message) {
        chatDao.create(chatId, message);
    }

    /** {@inheritDoc} */
    @Override
    public List<ChatMessage> listMessages(String chatId) {
        return chatDao.findByChatId(chatId);
    }
}
