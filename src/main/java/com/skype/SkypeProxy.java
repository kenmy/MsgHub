package com.skype;

public final class SkypeProxy {

    public static Chat getChat(String chatID){
        return Chat.getInstance(chatID);
    }
}
