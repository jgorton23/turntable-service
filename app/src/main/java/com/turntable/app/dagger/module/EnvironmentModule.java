package com.turntable.app.dagger.module;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
public class EnvironmentModule {

    public static final String AWS_REGION = "awsRegion";
    public static final String USERS_TABLE_NAME = "usersTableName";
    public static final String GAMES_TABLE_NAME = "gamesTableName";
    public static final String PLAYER_GAMES_TABLE_NAME = "playerGamesTableName";
    public static final String MOVES_TABLE_NAME = "movesTableName";
    public static final String FRIENDS_TABLE_NAME = "friendsTableName";
    public static final String CHAT_TABLE_NAME = "chatTableName";

    @Provides
    @Named(AWS_REGION)
    static String provideAwsRegion() {
        return System.getenv("AWS_REGION");
    }

    @Provides
    @Named(USERS_TABLE_NAME)
    static String provideUsersTableName() {
        return System.getenv("USERS_TABLE_NAME");
    }

    @Provides
    @Named(GAMES_TABLE_NAME)
    static String provideGamesTableName() {
        return System.getenv("GAMES_TABLE_NAME");
    }

    @Provides
    @Named(PLAYER_GAMES_TABLE_NAME)
    static String providePlayerGamesTableName() {
        return System.getenv("PLAYER_GAMES_TABLE_NAME");
    }

    @Provides
    @Named(MOVES_TABLE_NAME)
    static String provideMovesTableName() {
        return System.getenv("MOVES_TABLE_NAME");
    }

    @Provides
    @Named(FRIENDS_TABLE_NAME)
    static String provideFriendsTableName() {
        return System.getenv("FRIENDS_TABLE_NAME");
    }

    @Provides
    @Named(CHAT_TABLE_NAME)
    static String provideChatTableName() {
        return System.getenv("CHAT_TABLE_NAME");
    }
}
