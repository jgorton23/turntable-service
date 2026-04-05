package com.turntable.client.game;

import java.util.List;

/** Defines data access operations for games. */
public interface IGameDao {

    /**
     * Persists a new game.
     *
     * @param game the game to create
     * @return the created game
     */
    Game create(Game game);

    /**
     * Retrieves a game by its ID.
     *
     * @param gameId the ID of the game
     * @return the game, or null if not found
     */
    Game findById(String gameId);

    /**
     * Retrieves all games for a player, optionally filtered by status.
     *
     * @param playerId the ID of the player
     * @param status   the game status to filter by, or null for all games
     * @return the list of matching games
     */
    List<Game> findByPlayer(String playerId, GameStatus status);

    /**
     * Updates an existing game.
     *
     * @param gameId the ID of the game to update
     * @param game   the updated game data
     */
    void update(String gameId, Game game);

    /**
     * Retrieves all moves made in a game.
     *
     * @param gameId the ID of the game
     * @return the ordered list of moves
     */
    List<Move> findMoves(String gameId);

    /**
     * Persists a move made in a game.
     *
     * @param gameId the ID of the game
     * @param move   the move to persist
     */
    void createMove(String gameId, Move move);
}
