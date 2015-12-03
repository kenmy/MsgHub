package com.epam.messagehub;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterReader implements IReader {

    private Twitter twitter;
    private String twitter_user_id;
    
    TwitterReader(String atwitter_user_id){
        twitter = new TwitterFactory().getInstance();
        twitter_user_id = atwitter_user_id;
    }
    @Override
    public List<IMessage> getMessages() {
        List<IMessage> result = new ArrayList<IMessage>(); 
        List<Status> statuses;
        try {
            statuses = twitter.getUserTimeline(twitter_user_id);
            for (Status status : statuses) {
                result.add(new TwitterMessage(status, twitter_user_id));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return result;
    }

}
