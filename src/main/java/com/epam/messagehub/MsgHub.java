package com.epam.messagehub;

import info.usbo.skypetwitter.Run;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.skype.SkypeException;

public class MsgHub {
    private IReader reader;
    private IWriter writer;
    private ISaver saver;
    
    public MsgHub() throws IOException, SkypeException{
        Properties props = new Properties();
        InputStream in = Run.class.getResourceAsStream("/skype.properties");
        props.load(in);
        in.close();
        writer = new SkypeWriter(props.getProperty("skype.chat_group_id"));
        in = Run.class.getResourceAsStream("/twitter4j.properties");
        props.load(in);
        in.close();
        reader = new TwitterReader(props.getProperty("twitter.user"));
        saver = new MemorySaver();
    }
    
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
            MsgHub msgHub = new MsgHub();
            msgHub.doCheck();
        } catch (IOException | SkypeException e) {
            e.printStackTrace();
        }
    }
    
}
