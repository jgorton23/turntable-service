package com.turntable.app.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/** API-level representation of a TurnTable game. */
@Value
@Builder
public class Game {
    String id;
    GameStatus status;
    Instant createdTimestamp;
    List<String> players;
    String currentPlayer;
    GameResult result;
}
