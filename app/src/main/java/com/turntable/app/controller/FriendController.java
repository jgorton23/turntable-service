package com.turntable.app.controller;

import com.turntable.app.AuthHandler;
import com.turntable.app.model.FriendStatus;
import com.turntable.app.model.User;
import com.turntable.app.service.FriendService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class FriendController {

    private final FriendService friendService;

    public void create(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        CreateFriendRequest body = ctx.bodyAsClass(CreateFriendRequest.class);
        friendService.sendFriendRequest(currentUser.getUserId(), body.getToUserId());
        ctx.status(204);
    }

    public void getAll(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        String statusParam = ctx.queryParam("status");
        FriendStatus status = statusParam != null ? FriendStatus.valueOf(statusParam) : null;
        ctx.json(friendService.listFriends(currentUser.getUserId(), status));
    }

    public void update(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        String friendId = ctx.pathParam("friend-id");
        UpdateFriendRequest body = ctx.bodyAsClass(UpdateFriendRequest.class);
        switch (body.getAction()) {
            case "ACCEPT" -> friendService.acceptFriendRequest(currentUser.getUserId(), friendId);
            case "DECLINE" -> friendService.declineFriendRequest(currentUser.getUserId(), friendId);
            default -> throw new BadRequestResponse("action must be ACCEPT or DECLINE");
        }
        ctx.status(204);
    }

    public void delete(Context ctx) {
        User currentUser = ctx.attribute(AuthHandler.USER_ATTRIBUTE);
        String friendId = ctx.pathParam("friend-id");
        friendService.removeFriend(currentUser.getUserId(), friendId);
        ctx.status(204);
    }

    @Data
    static class CreateFriendRequest {
        private String toUserId;
    }

    @Data
    static class UpdateFriendRequest {
        private String action;
    }
}
