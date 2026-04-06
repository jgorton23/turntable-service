package com.turntable.app.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/** API-level representation of a friend relationship. */
@Value
@Builder
public class Friend {
    String friendId;
    String friendUsername;
    FriendStatus status;
    Instant friendsSince;
}
