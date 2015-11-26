package com.epam.messagehub;

import java.util.HashSet;
import java.util.Set;

public class MemorySaver implements ISaver {

    private Set<Long> sendedIDs = new HashSet<Long>();
    
    @Override
    public boolean checkSaved(IMessage msg) {
        boolean result = sendedIDs.contains(msg);
        if(!result){
            sendedIDs.add(msg.getId());
        }
        return result;
    }

    @Override
    public void flush() {
    }

}
