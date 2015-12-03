package com.epam.messagehub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader extends Properties {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static PropertiesReader reader;
    
    public PropertiesReader(String[] files){
        for (String fileName : files){
            try {
                loadFromFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadFromFile(String fileName) throws IOException{
        InputStream in = PropertiesReader.class.getResourceAsStream("/" + fileName);
        load(in);
        in.close();
    }
    
    public static PropertiesReader getReader(){
        return reader == null ? reader = new PropertiesReader(new String[] {"skype.properties", "twitter4j.properties", "app.properties"}) : reader; 
        //     
    }
}
