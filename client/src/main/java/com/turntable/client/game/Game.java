package com.turntable.client.game;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class Game {
    private final String id;
    private final GameStatus status;
    private final Instant createdTimestamp;
    private final List<String> players;
    private final String currentPlayer;
    private final String result;
}
