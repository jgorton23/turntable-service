package com.turntable.client.game;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/** Represents a TurnTable game. */
@Data
@Builder
public class Game {

    /** The unique identifier for this game. */
    private final String id;

    /** The current status of the game (PENDING, STARTED, or ENDED). */
    private final GameStatus status;

    /** The timestamp when the game was created. */
    private final Instant createdTimestamp;

    /** The IDs of all players in this game. */
    private final List<String> players;

    /** The ID of the player whose turn it currently is. */
    private final String currentPlayer;

    /** The result of the game, or null if the game has not ended. */
    private final String result;
}
