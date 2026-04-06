package com.turntable.app.controller;

import com.turntable.app.service.UserService;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class UserController {

    private final UserService userService;

    public void getOne(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'getOne'");
    }

    public void update(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    public void delete(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
