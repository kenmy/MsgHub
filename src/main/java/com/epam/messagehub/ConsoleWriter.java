package com.epam.messagehub;

public class ConsoleWriter implements Writer {

    @Override
    public void writeMessage(Message msg) {
        System.out.println(msg.getId() + ":" + msg.getData());
    }

}
