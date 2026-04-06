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
        String chatId = ctx.pathParam("chat-id");
        ctx.json(chatService.listMessages(chatId));
    }

    public void webSocketEvents(WsConfig config) {}
}
