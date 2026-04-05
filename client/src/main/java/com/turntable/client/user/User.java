package com.turntable.client.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private final String userId;
    private final String username;
    private final String email;
    private final String avatar;
}
