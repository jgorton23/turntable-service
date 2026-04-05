package com.turntable.app.dagger.component;

import com.turntable.app.controller.ChatController;
import com.turntable.app.controller.FriendController;
import com.turntable.app.controller.GameController;
import com.turntable.app.controller.UserController;
import dagger.Component;

@Component
public interface AppComponent {
    UserController userController();
    FriendController friendController();
    GameController gameController();
    ChatController chatController();
}
