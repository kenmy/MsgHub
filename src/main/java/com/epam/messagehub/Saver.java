package com.epam.messagehub;

public interface Saver {
    boolean checkSaved(Message msg);
    void flush();
}
