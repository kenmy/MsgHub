package com.epam.messagehub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VKReader implements Reader {

    private Long userID;

    VKReader(long UserID) {
        userID = UserID;
    }

    @Override
    public List<Message> getMessages() {
        List<Message> messages = new ArrayList<Message>();

        String url = "https://api.vk.com/method/wall.get?v=5.40&owner_id=" + userID + "&filter=owner&extended=1";
        BufferedReader reader = null;
        try {
            URL url2 = new URL(url);
            reader = new BufferedReader(new InputStreamReader(url2.openStream(), "UTF-8"));
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
            json = (JSONObject) new JSONParser().parse(json.get("response").toString());
            JSONArray jsona = (JSONArray) new JSONParser().parse(json.get("items").toString());
            for (int i = 0; i < jsona.size(); i++) {
                messages.add(new VKMessage(jsona.get(i).toString(), userID));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }

}
