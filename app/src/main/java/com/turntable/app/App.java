package com.turntable.app;

import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.ws;

public class App {
    public static void main(String[] args) {
        Javalin.create(config -> {
            config.routes.apiBuilder(() -> {
                crud("/users/{user-id}", new UserController());
                crud("/friends/{friend-id}", new FriendController());
                crud("/games/{game-id}", new GameController());
                ws("/games/{game-id}/ws", GameController::webSocketEvents);
                ws("/chat/{chat-id}/ws", ChatController::webSocketEvents);
            });
        }).start();
    }
}
