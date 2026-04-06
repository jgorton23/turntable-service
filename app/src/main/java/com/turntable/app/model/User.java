package com.turntable.app.model;

import lombok.Builder;
import lombok.Value;

/** API-level representation of a TurnTable user. */
@Value
@Builder
public class User {
    String userId;
    String username;
    String email;
    String avatar;
}
