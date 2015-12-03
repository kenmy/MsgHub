package com.epam.messagehub;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VKMessage implements Message {

    private long id;
    private String text;
    private Date date;
    private String attachment;
    private long groupID;
    
    VKMessage(String line, Long groupID){
        this.groupID = groupID;
        JSONObject json2;
        try {
            json2 = (JSONObject) new JSONParser().parse(line);
            id = Long.parseLong(json2.get("id").toString());
            text = json2.get("text").toString();
            if ("".equals(text)) {
                if (json2.get("copy_history") != null) {
                    JSONArray json3 = (JSONArray) new JSONParser().parse(json2.get("copy_history").toString());
                    if (json3.size() > 0) {
                        JSONObject json4 = (JSONObject) new JSONParser().parse(json3.get(0).toString());
                        text = json4.get("text").toString();
                    }
                } else {
                    text = "";
                }
            }
            date = new Date(Long.parseLong(json2.get("date").toString()) * 1000);
            attachment = findAttachment(json2);
        } catch (ParseException e) {
            text = "";
            e.printStackTrace();
        }
    }
    
    private String findAttachment(JSONObject js) {
        String att = "";
        System.out.println("att0" + js.get("id").toString());
        if (js.get("attachments") != null) {
            System.out.println("att1");
            JSONArray attach;
            try {
                attach = (JSONArray) new JSONParser().parse(js.get("attachments").toString());
                if (attach.size() > 0) {
                    System.out.println("att2");
                    JSONObject attach1 = (JSONObject) new JSONParser().parse(attach.get(0).toString());
                    if (attach1.get("type") != null && "photo".equals(attach1.get("type").toString())) {
                        System.out.println("att3");
                        JSONObject photo = (JSONObject) new JSONParser().parse(attach1.get("photo").toString());
                        if (photo.get("photo_604") != null) {
                            att = photo.get("photo_604").toString();
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return att;
    }
    
    @Override
    public String getData() {
        Date d = date;
        // Добавим часов
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR_OF_DAY, 7);
        d = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");        
        String sText = "@Epam_samara (VK) от " + sdf.format(d) + " ( http://vk.com/wall" + groupID + "_"
                + id + " ): \r\n" + text;
        if (!"".equals(attachment)) {
            sText += "\r\n" + attachment;
        }
        sText += "\r\n***";
        System.out.println(sText);
        return sText;
    }              

    @Override
    public Long getId() {
        return id;
    }

}
