package com.turntable.app.dagger.module;

import com.turntable.client.chat.IChatDao;
import com.turntable.client.friend.IFriendDao;
import com.turntable.client.game.IGameDao;
import com.turntable.client.user.IUserDao;
import com.turntable.ddb.DynamoChatDao;
import com.turntable.ddb.DynamoFriendDao;
import com.turntable.ddb.DynamoGameDao;
import com.turntable.ddb.DynamoUserDao;
import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.turntable.app.dagger.module.EnvironmentModule.AWS_REGION;
import static com.turntable.app.dagger.module.EnvironmentModule.CHAT_TABLE_NAME;
import static com.turntable.app.dagger.module.EnvironmentModule.FRIENDS_TABLE_NAME;
import static com.turntable.app.dagger.module.EnvironmentModule.GAMES_TABLE_NAME;
import static com.turntable.app.dagger.module.EnvironmentModule.MOVES_TABLE_NAME;
import static com.turntable.app.dagger.module.EnvironmentModule.PLAYER_GAMES_TABLE_NAME;
import static com.turntable.app.dagger.module.EnvironmentModule.USERS_TABLE_NAME;

@Module(includes = EnvironmentModule.class)
public class DaoModule {

    @Provides
    @Singleton
    static DynamoDbClient provideDynamoDbClient(@Named(AWS_REGION) String region) {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Provides
    @Singleton
    static IUserDao provideUserDao(DynamoDbClient client, @Named(USERS_TABLE_NAME) String tableName) {
        return new DynamoUserDao(client, tableName);
    }

    @Provides
    @Singleton
    static IGameDao provideGameDao(
            DynamoDbClient client,
            @Named(GAMES_TABLE_NAME) String gamesTableName,
            @Named(PLAYER_GAMES_TABLE_NAME) String playerGamesTableName,
            @Named(MOVES_TABLE_NAME) String movesTableName) {
        return new DynamoGameDao(client, gamesTableName, playerGamesTableName, movesTableName);
    }

    @Provides
    @Singleton
    static IFriendDao provideFriendDao(DynamoDbClient client, @Named(FRIENDS_TABLE_NAME) String tableName) {
        return new DynamoFriendDao(client, tableName);
    }

    @Provides
    @Singleton
    static IChatDao provideChatDao(DynamoDbClient client, @Named(CHAT_TABLE_NAME) String tableName) {
        return new DynamoChatDao(client, tableName);
    }
}
