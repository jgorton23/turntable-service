package com.turntable.app.dagger.module;

import com.turntable.client.TurnTableClient;
import com.turntable.client.chat.IChatClient;
import com.turntable.client.chat.IChatDao;
import com.turntable.client.friend.IFriendClient;
import com.turntable.client.friend.IFriendDao;
import com.turntable.client.game.IGameClient;
import com.turntable.client.game.IGameDao;
import com.turntable.client.user.IUserClient;
import com.turntable.client.user.IUserDao;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(includes = DaoModule.class)
public class ClientModule {

    @Provides
    @Singleton
    static TurnTableClient provideTurnTableClient(
            IUserDao userDao, IGameDao gameDao, IFriendDao friendDao, IChatDao chatDao) {
        return TurnTableClient.builder()
                .userDao(userDao)
                .gameDao(gameDao)
                .friendDao(friendDao)
                .chatDao(chatDao)
                .build();
    }

    @Provides
    static IUserClient provideUserClient(TurnTableClient client) {
        return client;
    }

    @Provides
    static IGameClient provideGameClient(TurnTableClient client) {
        return client;
    }

    @Provides
    static IFriendClient provideFriendClient(TurnTableClient client) {
        return client;
    }

    @Provides
    static IChatClient provideChatClient(TurnTableClient client) {
        return client;
    }
}
