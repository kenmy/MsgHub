package com.epam.messagehub;

import java.io.IOException;
import java.util.List;

import com.skype.SkypeException;

public class MsgHub {
    private IReader reader;
    private IWriter writer;
    private ISaver saver;
    
    public MsgHub(IReader areader, IWriter awriter, ISaver asaver){
        reader = areader;
        writer = awriter;
        saver = asaver;
    }
    
    public void doCheck(){
        List<IMessage> messages = reader.getMessages();
        for(IMessage message : messages){
            if(!saver.checkSaved(message)){
                writer.writeMessage(message);
                saver.flush();
            }
        }
    }
    
    public static void main(String[] args){
        try {
            PropertiesReader props = PropertiesReader.getReader();
            IWriter writer = new SkypeWriter(props.getProperty("skype.chat_group_id"));
            IReader reader = new TwitterReader(props.getProperty("twitter.user"));
            ISaver saver = new MemorySaver();
            MsgHub msgHub = new MsgHub(reader, writer, saver);
            msgHub.doCheck();
        } catch (IOException | SkypeException e) {
            e.printStackTrace();
        }
    }
    
}
