package com.turntable.client.game;

import lombok.Builder;
import lombok.Data;

/** Represents the result of an ended game. */
@Data
@Builder
public class GameResult {

    /** The ID of the winning player, or null if the game ended in a draw. */
    private final String winnerId;
}
