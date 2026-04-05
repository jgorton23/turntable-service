package com.turntable.client.user;

import lombok.Builder;
import lombok.Data;

/** Represents a TurnTable user. */
@Data
@Builder
public class User {

    /** The unique identifier for this user. */
    private final String userId;

    /** The user's display name. */
    private final String username;

    /** The user's email address, or null if not provided. */
    private final String email;

    /** The URL of the user's avatar image, or null if not provided. */
    private final String avatar;
}
