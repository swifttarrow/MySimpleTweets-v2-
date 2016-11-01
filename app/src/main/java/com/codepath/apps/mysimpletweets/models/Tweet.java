package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    private String body;
    private long uid;
    private User user;
    private String createdAt;

    public long getUid() {return uid;}
    public String getBody() {return body;}
    public User getUser() {return user;}
    public String getCreatedAt() { return DateUtils.getRelativeTimeAgo(createdAt); }

    public static Tweet fromJSON(JSONObject jsonObject){
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static List<Tweet> fromJSONArray(JSONArray jsonArray){
        List<Tweet> tweets = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Tweet tweet = fromJSON(jsonObj);
                if (tweet != null){
                    tweets.add(tweet);
                }
            } catch (JSONException e){
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }
}
