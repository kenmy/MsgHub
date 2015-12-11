package com.epam.messagehub;

import java.io.IOException;

import com.skype.Chat;
import com.skype.SkypeException;
import com.skype.SkypeProxy;

public class SkypeWriter implements Writer {

    private Chat chat; 
    
    SkypeWriter(String skype_chat_id) throws IOException, SkypeException{
        chat = SkypeProxy.getChat(skype_chat_id);
    }
    
    @Override
    public void writeMessage(Message msg) {
        try {
            chat.send(msg.getData());
        } catch (SkypeException e) {
            e.printStackTrace();
        }
    }

}
