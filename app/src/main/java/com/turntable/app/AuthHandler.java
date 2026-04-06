package com.turntable.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turntable.app.model.User;
import com.turntable.app.service.UserService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Base64;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AuthHandler implements Handler {

    public static final String USER_ATTRIBUTE = "user";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final UserService userService;

    @Override
    public void handle(Context ctx) throws Exception {
        String userId = ctx.header("x-amzn-oidc-identity");
        if (userId == null) {
            throw new UnauthorizedResponse();
        }

        User user = userService.getUser(userId);
        if (user == null) {
            user = userService.createUser(buildUser(userId, ctx));
        }

        ctx.attribute(USER_ATTRIBUTE, user);
    }

    private User buildUser(String userId, Context ctx) throws Exception {
        String name = null;
        String email = null;

        String oidcData = ctx.header("x-amzn-oidc-data");
        if (oidcData != null) {
            String[] parts = oidcData.split("\\.");
            if (parts.length >= 2) {
                String payload = parts[1];
                int mod = payload.length() % 4;
                if (mod != 0) payload += "=".repeat(4 - mod);
                JsonNode claims = MAPPER.readTree(Base64.getUrlDecoder().decode(payload));
                name = claims.path("name").asText(null);
                email = claims.path("email").asText(null);
            }
        }

        return User.builder()
                .userId(userId)
                .username(name != null ? name : userId)
                .email(email)
                .build();
    }
}
