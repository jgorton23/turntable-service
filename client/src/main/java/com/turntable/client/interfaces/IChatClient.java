package com.turntable.client.interfaces;

public interface IChatClient {
    // SendChat
    // ListChats
    void createChat();
    void getChat(String chatId);
    void getAllChats();
    void updateChat(String chatId);
    void deleteChat(String chatId);
}
