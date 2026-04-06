package com.turntable.app.service;

import com.turntable.app.model.Game;
import com.turntable.app.model.GameResult;
import com.turntable.app.model.GameStatus;
import com.turntable.app.model.Move;
import com.turntable.client.game.IGameClient;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GameService {

    private final IGameClient gameClient;

    public Game createGame(Game game) {
        return toModel(gameClient.createGame(toClientModel(game)));
    }

    public Game getGame(String gameId) {
        com.turntable.client.game.Game game = gameClient.getGame(gameId);
        return game == null ? null : toModel(game);
    }

    public List<Game> listGames(String playerId, GameStatus status) {
        return gameClient.listGames(playerId, toClientStatus(status))
                .stream()
                .map(GameService::toModel)
                .collect(Collectors.toList());
    }

    public void cancelGame(String gameId) {
        gameClient.cancelGame(gameId);
    }

    public void joinGame(String gameId, String playerId) {
        gameClient.joinGame(gameId, playerId);
    }

    public void startGame(String gameId) {
        gameClient.startGame(gameId);
    }

    public void endGame(String gameId, GameResult result) {
        gameClient.endGame(gameId, toClientResult(result));
    }

    public List<Move> listMoves(String gameId) {
        return gameClient.listMoves(gameId)
                .stream()
                .map(GameService::toMoveModel)
                .collect(Collectors.toList());
    }

    public void doMove(String gameId, String playerId, Move move) {
        gameClient.doMove(gameId, playerId, toClientMove(move));
    }

    private static Game toModel(com.turntable.client.game.Game g) {
        return Game.builder()
                .id(g.getId())
                .status(GameStatus.valueOf(g.getStatus().name()))
                .createdTimestamp(g.getCreatedTimestamp())
                .players(g.getPlayers())
                .currentPlayer(g.getCurrentPlayer())
                .result(g.getResult() == null ? null : toResultModel(g.getResult()))
                .build();
    }

    private static com.turntable.client.game.Game toClientModel(Game g) {
        return com.turntable.client.game.Game.builder()
                .id(g.getId())
                .status(toClientStatus(g.getStatus()))
                .createdTimestamp(g.getCreatedTimestamp())
                .players(g.getPlayers())
                .currentPlayer(g.getCurrentPlayer())
                .result(g.getResult() == null ? null : toClientResult(g.getResult()))
                .build();
    }

    private static Move toMoveModel(com.turntable.client.game.Move m) {
        return Move.builder()
                .gameId(m.getGameId())
                .userId(m.getUserId())
                .move(m.getMove())
                .moveTimestamp(m.getMoveTimestamp())
                .build();
    }

    private static com.turntable.client.game.Move toClientMove(Move m) {
        return com.turntable.client.game.Move.builder()
                .gameId(m.getGameId())
                .userId(m.getUserId())
                .move(m.getMove())
                .moveTimestamp(m.getMoveTimestamp())
                .build();
    }

    private static GameResult toResultModel(com.turntable.client.game.GameResult r) {
        return GameResult.builder()
                .winnerId(r.getWinnerId())
                .build();
    }

    private static com.turntable.client.game.GameResult toClientResult(GameResult r) {
        return com.turntable.client.game.GameResult.builder()
                .winnerId(r.getWinnerId())
                .build();
    }

    private static com.turntable.client.game.GameStatus toClientStatus(GameStatus status) {
        return status == null ? null : com.turntable.client.game.GameStatus.valueOf(status.name());
    }
}
