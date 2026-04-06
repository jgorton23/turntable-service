package com.turntable.app.dagger.component;

import com.turntable.app.AuthHandler;
import com.turntable.app.controller.ChatController;
import com.turntable.app.controller.FriendController;
import com.turntable.app.controller.GameController;
import com.turntable.app.controller.UserController;
import com.turntable.app.dagger.module.ClientModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ClientModule.class)
public interface AppComponent {
    AuthHandler authHandler();
    UserController userController();
    FriendController friendController();
    GameController gameController();
    ChatController chatController();
}
