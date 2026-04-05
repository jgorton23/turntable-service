package com.turntable.client.game;

import java.util.List;

public interface IGameClient {
    Game createGame(Game game);
    Game getGame(String gameId);
    List<Game> listGames(String playerId, GameStatus status);
    void cancelGame(String gameId);
    void joinGame(String gameId, String playerId);
    void startGame(String gameId);
    void endGame(String gameId);
    List<Move> listMoves(String gameId);
    void doMove(String gameId, String playerId, Move move);
}
