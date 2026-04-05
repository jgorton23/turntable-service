package com.turntable.client.game;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/** Represents a move made by a player in a game. */
@Data
@Builder
public class Move {

    /** The ID of the game this move belongs to. */
    private final String gameId;

    /** The ID of the player who made this move. */
    private final String userId;

    /** The move data. */
    private final String move;

    /** The timestamp when the move was made. */
    private final Instant moveTimestamp;
}
