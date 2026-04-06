package com.turntable.app.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/** API-level representation of a move made in a game. */
@Value
@Builder
public class Move {
    String gameId;
    String userId;
    String move;
    Instant moveTimestamp;
}
