package com.turntable.app.controller;

import com.turntable.app.AuthHandler;
import com.turntable.app.model.User;
import com.turntable.app.service.UserService;
import io.javalin.http.Context;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class UserController {

    private final UserService userService;

    public void getOne(Context ctx) {
        ctx.json(ctx.attribute(AuthHandler.USER_ATTRIBUTE));
    }

    public void update(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        UpdateUserRequest body = ctx.bodyAsClass(UpdateUserRequest.class);
        userService.updateUser(currentUser.getUserId(), User.builder()
                .userId(currentUser.getUserId())
                .username(body.getUsername())
                .email(body.getEmail())
                .avatar(body.getAvatar())
                .build());
        ctx.status(204);
    }

    public void delete(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        userService.deleteUser(currentUser.getUserId());
        ctx.status(204);
    }

    @Data
    static class UpdateUserRequest {
        private String username;
        private String email;
        private String avatar;
    }
}
