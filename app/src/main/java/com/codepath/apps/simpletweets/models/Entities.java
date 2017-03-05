package com.codepath.apps.simpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by reneewu on 3/5/2017.
 */
@Parcel
public class Entities {

    private List<Media> media = null;
    public List<Media> getMedia() {
        return media;
    }

    public Entities(){}

    public static Entities fromJSON(JSONObject jsonObject){

        Entities extendedEntities = new Entities();

        try {
            extendedEntities.media = Media.fromJSONArray(jsonObject.getJSONArray("media"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return extendedEntities;
    }
}
