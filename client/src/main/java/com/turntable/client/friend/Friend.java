package com.turntable.client.friend;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Friend {
    private final String username;
    private final FriendStatus status;
    private final Instant friendsSince;
}
