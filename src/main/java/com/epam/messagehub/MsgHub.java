package com.epam.messagehub;

import java.io.IOException;
import java.util.List;

import com.skype.SkypeException;

public class MsgHub {
    private Reader reader;
    private Writer writer;
    private Saver saver;
    
    public MsgHub(Reader areader, Writer awriter, Saver asaver){
        reader = areader;
        writer = awriter;
        saver = asaver;
    }
    
    public void doCheck(){
        List<Message> messages = reader.getMessages();
        for(Message message : messages){
            if(!saver.checkSaved(message)){
                writer.writeMessage(message);
                saver.flush();
            }
        }
    }
    
    public static void main(String[] args){
        try {
            PropertiesReader props = PropertiesReader.getReader();
            Writer writer = null;
            Boolean IsTest = true;
            if (IsTest){
                writer = new ConsoleWriter();
            } else {
                writer = new SkypeWriter(props.getProperty("skype.chat_group_id"));
            }
            Reader reader = null;
            if (IsTest){
                reader = new VKReader(Long.valueOf(props.getProperty("vk.groupID")));
            } else {
                reader = new TwitterReader(props.getProperty("twitter.user"));
            }
            Saver saver = new MemorySaver();
            MsgHub msgHub = new MsgHub(reader, writer, saver);
            msgHub.doCheck();
        } catch (IOException | SkypeException e) {
            e.printStackTrace();
        }
    }
    
}
