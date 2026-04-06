package com.turntable.app.model;

import lombok.Builder;
import lombok.Value;

/** Represents the result of an ended game. */
@Value
@Builder
public class GameResult {
    String winnerId;
}
