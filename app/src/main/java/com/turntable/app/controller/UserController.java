package com.turntable.app.controller;

import com.turntable.app.service.UserService;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class UserController implements CrudHandler {

    private final UserService userService;

    @Override
    public void create(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void delete(Context ctx, String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void getAll(Context ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public void getOne(Context ctx, String userId) {
        ctx.result(userId);
    }

    @Override
    public void update(Context ctx, String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
