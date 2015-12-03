package com.epam.messagehub;

import java.io.IOException;
import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;

public class SkypeWriter implements Writer {

    private Chat ch; 
    
    SkypeWriter(String skype_chat_id) throws IOException, SkypeException{
        ch = Skype.chat(skype_chat_id);
    }
    @Override
    public void writeMessage(Message msg) {
        try {
            ch.send(msg.getData());
        } catch (SkypeException e) {
            e.printStackTrace();
        }
    }

}
