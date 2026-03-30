package com.turntable.app;

import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.ws;

public class App {
    public static void main(String[] args) {
        Javalin.create(config -> {
            config.routes.apiBuilder(() -> {
                crud("", new UserController());
                crud("", new FriendController());
                crud("", new GameController());
                ws("", GameController::gameEvents);
                ws("", GameController::chatEvents);
            });
        }).start();
    }
}
