package com.turntable.client.interfaces;

public interface IGameClient {
    // ListGames (filter by pending, started, ended)
    // CreateGame
    // CancelGame
    // JoinGame
    // StartGame
    // EndGame?
    // GetGame
    // ListMoves
    // DoMove
    void createGame();
    void getGame(String gameId);
    void getAllGames();
    void updateGame(String gameId);
    void deleteGame(String gameId);
}
