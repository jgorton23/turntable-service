package com.turntable.client.game;

import java.util.List;

/** Defines game operations. */
public interface IGameClient {

    /**
     * Creates a new game.
     *
     * @param game the game to create
     * @return the created game
     */
    Game createGame(Game game);

    /**
     * Retrieves a game by its ID.
     *
     * @param gameId the ID of the game
     * @return the game
     */
    Game getGame(String gameId);

    /**
     * Lists games for a player, optionally filtered by status.
     *
     * @param playerId the ID of the player
     * @param status   the game status to filter by, or null for all games
     * @return the list of matching games
     */
    List<Game> listGames(String playerId, GameStatus status);

    /**
     * Cancels a game.
     *
     * @param gameId the ID of the game to cancel
     */
    void cancelGame(String gameId);

    /**
     * Adds a player to a game.
     *
     * @param gameId   the ID of the game to join
     * @param playerId the ID of the player joining
     */
    void joinGame(String gameId, String playerId);

    /**
     * Starts a game, transitioning it from PENDING to STARTED.
     *
     * @param gameId the ID of the game to start
     */
    void startGame(String gameId);

    /**
     * Ends a game, transitioning it from STARTED to ENDED.
     *
     * @param gameId the ID of the game to end
     */
    void endGame(String gameId);

    /**
     * Lists all moves made in a game.
     *
     * @param gameId the ID of the game
     * @return the ordered list of moves
     */
    List<Move> listMoves(String gameId);

    /**
     * Submits a move for a player in a game.
     *
     * @param gameId   the ID of the game
     * @param playerId the ID of the player making the move
     * @param move     the move to submit
     */
    void doMove(String gameId, String playerId, Move move);
}
