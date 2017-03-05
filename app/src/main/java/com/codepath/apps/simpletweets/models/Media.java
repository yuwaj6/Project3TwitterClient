package com.codepath.apps.simpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reneewu on 3/5/2017.
 */

@Parcel
public class Media {
    private String mediaUrl;
    private String type;
    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getType() {
        return type;
    }

    public Media (){}

    public static Media fromJSON(JSONObject jsonObject){

        Media media = new Media();

        try {
            media.mediaUrl = jsonObject.getString("media_url");
            media.type = jsonObject.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return media;
    }

    public static ArrayList<Media> fromJSONArray(JSONArray jsonArray){
        ArrayList<Media> medias = new ArrayList<>();

        for(int i =0;i<jsonArray.length();i++){
            JSONObject mediaJson = null;
            try {
                mediaJson = jsonArray.getJSONObject(i);
                Media m = Media.fromJSON(mediaJson);
                if(m!=null){
                    medias.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return medias;
    }

    public static Media getPhotoMedia(List<Media> entities){
        if(entities==null)
            return null;

        for( Media media : entities) {
            if(media.getType().equals("photo")) {
                return media;
            }
        }
        return null;
    }
}
