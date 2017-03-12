package com.codepath.apps.simpletweets.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by reneewu on 3/8/2017.
 */

public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long currentMaxId;
    private int currentPage = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        Log.d("debug", "UserTimelineFragment::OnCreate");
        populateTimeline(0);
    }

    public static UserTimelineFragment newInstance(String screen_name){
        UserTimelineFragment userFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screen_name);
        userFragment.setArguments(args);
        return userFragment;
    }

    @Override
    public void loadNextDataFromApi(int offset) {
        if (offset > currentPage)
            populateTimeline(currentMaxId);

        if (offset == 0)
            populateTimeline(0);

        currentPage = offset;
    }

    public void populateTimeline(final long maxId) {
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeLine(screenName, maxId, new JsonHttpResponseHandler() {
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("debug", json.toString());
                if (maxId == 0)
                    tweets.clear();

                ArrayList<Tweet> newT = Tweet.fromJSONArray(json);
                if (newT.size() > 0) {
                    currentMaxId = newT.get(newT.size() - 1).getUid();

                    tweets.addAll(newT);
                    aTweets.notifyDataSetChanged();
                }

                swipeContainer.setRefreshing(false);
                Log.d("debug", "UserTimelineFragment::populateTimeline Success");
            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug", errorResponse.toString());
            }
        });
    }
}