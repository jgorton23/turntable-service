package com.turntable.client.friend;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Friend {

    /** The friend's display name. */
    private final String username;

    /** The current status of the friendship (ACCEPTED, INVITATION_SENT, INVITATION_RECEIVED). */
    private final FriendStatus status;

    /** The timestamp when the friendship was established, or null if not yet accepted. */
    private final Instant friendsSince;
}
