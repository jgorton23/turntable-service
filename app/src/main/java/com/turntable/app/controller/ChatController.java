package com.turntable.app.controller;

import com.turntable.app.service.ChatService;
import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatController {

    private final ChatService chatService;

    public void getOne(Context ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'getOne'");
    }

    public void webSocketEvents(WsConfig config) {}
}
