package com.epam.messagehub;

public interface ISaver {
    boolean checkSaved(IMessage msg);
    void flush();
}
