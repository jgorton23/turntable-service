package com.turntable.client.game;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Move {
    private final String gameId;
    private final String userId;
    private final String move;
    private final Instant moveTimestamp;
}
