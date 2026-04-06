package com.turntable.app.controller;

import com.turntable.app.AuthHandler;
import com.turntable.app.model.Game;
import com.turntable.app.model.GameResult;
import com.turntable.app.model.GameStatus;
import com.turntable.app.model.User;
import com.turntable.app.service.GameService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.websocket.WsConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameController {

    private final GameService gameService;

    public void create(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        Game game = gameService.createGame(Game.builder()
                .id(UUID.randomUUID().toString())
                .status(GameStatus.PENDING)
                .createdTimestamp(Instant.now())
                .players(new ArrayList<>())
                .currentPlayer(currentUser.getUserId())
                .build());
        ctx.status(201).json(game);
    }

    public void getAll(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        String statusParam = ctx.queryParam("status");
        GameStatus status = statusParam != null ? GameStatus.valueOf(statusParam) : null;
        ctx.json(gameService.listGames(currentUser.getUserId(), status));
    }

    public void getOne(Context ctx) {
        String gameId = ctx.pathParam("game-id");
        Game game = gameService.getGame(gameId);
        if (game == null) throw new NotFoundResponse();
        ctx.json(game);
    }

    public void update(Context ctx) {
        String gameId = ctx.pathParam("game-id");
        UpdateGameRequest body = ctx.bodyAsClass(UpdateGameRequest.class);
        switch (body.getStatus()) {
            case STARTED -> gameService.startGame(gameId);
            case CANCELLED -> gameService.cancelGame(gameId);
            case ENDED -> {
                if (body.getResult() == null) throw new BadRequestResponse("result is required when ending a game");
                gameService.endGame(gameId, body.getResult());
            }
            default -> throw new BadRequestResponse("status must be STARTED, CANCELLED, or ENDED");
        }
        ctx.status(204);
    }

    public void joinGame(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        String gameId = ctx.pathParam("game-id");
        gameService.joinGame(gameId, currentUser.getUserId());
        ctx.status(204);
    }

    public void listMoves(Context ctx) {
        String gameId = ctx.pathParam("game-id");
        ctx.json(gameService.listMoves(gameId));
    }

    public void webSocketEvents(WsConfig config) {}

    @Data
    static class UpdateGameRequest {
        private GameStatus status;
        private GameResult result;
    }
}
