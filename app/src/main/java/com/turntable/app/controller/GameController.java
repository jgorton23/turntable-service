package com.turntable.app.controller;

import com.turntable.app.service.GameService;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameController {

    private final GameService gameService;

    public void create(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    public void getAll(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    public void getOne(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'getOne'");
    }

    public void update(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void joinGame(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
    }

    public void listMoves(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'listMoves'");
    }

    public void webSocketEvents(WsConfig config) {}
}
