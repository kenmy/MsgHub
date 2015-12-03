package com.epam.messagehub;

import java.text.SimpleDateFormat;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

public class TwitterMessage implements Message {

    private String sText;
    private Long id;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    
    TwitterMessage(Status twitterMessage, String twitter_user_id){
        id = twitterMessage.getId();
        sText = "@" + twitterMessage.getUser().getScreenName() + " от " + sdf.format(twitterMessage.getCreatedAt()) + " ( https://twitter.com/"
                + twitter_user_id + "/status/" + id + " ): \r\n" + twitterMessage.getText() + "\r\n***";
        for (URLEntity e : twitterMessage.getURLEntities()) {
            sText = sText.replaceAll(e.getURL(), e.getExpandedURL());
        }
        for (MediaEntity e : twitterMessage.getMediaEntities()) {
            sText = sText.replaceAll(e.getURL(), e.getMediaURL());
        }
    }
    
    @Override
    public String getData() {
        return sText;
    }

    @Override
    public Long getId() {
        return id;
    }

}
