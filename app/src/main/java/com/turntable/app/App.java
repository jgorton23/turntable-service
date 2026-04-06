package com.turntable.app;

import com.turntable.app.dagger.component.AppComponent;
import com.turntable.app.dagger.component.DaggerAppComponent;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
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
                crud("/users/{user-id}", component.userController());
                crud("/friends/{friend-id}", component.friendController());
                crud("/games/{game-id}", component.gameController());
                crud("/chat/{chat-id}", component.chatController());
                ws("/games/{game-id}/ws", component.gameController()::webSocketEvents);
                ws("/chat/{chat-id}/ws", component.chatController()::webSocketEvents);
            });
        }).start();
    }
}
