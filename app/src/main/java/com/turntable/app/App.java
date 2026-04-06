package com.turntable.app;

import com.turntable.app.dagger.component.AppComponent;
import com.turntable.app.dagger.component.DaggerAppComponent;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.ws;

public class App {
    public static void main(String[] args) {
        AppComponent component = DaggerAppComponent.create();

        Javalin.create(config -> {
            config.router.mount(router -> {
                router.beforeMatched(ctx -> {
                    if (!ctx.path().equals("/health")) {
                        component.authHandler().handle(ctx);
                    }
                });
            });
            config.routes.apiBuilder(() -> {
                get("/health", ctx -> ctx.status(200));
                path("/profile", () -> {
                    get(component.userController()::getOne);
                    patch(component.userController()::update);
                    delete(component.userController()::delete);
                });
                path("/friends", () -> {
                    post(component.friendController()::create);
                    get(component.friendController()::getAll);
                    path("/{friend-id}", () -> {
                        patch(component.friendController()::update);
                        delete(component.friendController()::delete);
                    });
                });
                path("/games", () -> {
                    post(component.gameController()::create);
                    get(component.gameController()::getAll);
                    path("/{game-id}", () -> {
                        get(component.gameController()::getOne);
                        patch(component.gameController()::update);
                        post("/players", component.gameController()::joinGame);
                        get("/moves", component.gameController()::listMoves);
                        ws("/ws", component.gameController()::webSocketEvents);
                    });
                });
                path("/chat/{chat-id}", () -> {
                    get(component.chatController()::getOne);
                    ws("/ws", component.chatController()::webSocketEvents);
                });
            });
        }).start();
    }
}
