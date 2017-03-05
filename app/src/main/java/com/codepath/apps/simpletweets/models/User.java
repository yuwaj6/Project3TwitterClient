package com.codepath.apps.simpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by reneewu on 3/3/2017.
 */

@Parcel
public class User {
    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return "@"+ screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;

    public User(){}

    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid=jsonObject.getLong("id");
            user.screenName=jsonObject.getString("screen_name");
            user.profileImageUrl= jsonObject.getString("profile_image_url");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }
}
