package com.turntable.app.controller;

import com.turntable.app.service.FriendService;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class FriendController {

    private final FriendService friendService;

    public void create(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    public void getAll(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    public void update(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void delete(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
