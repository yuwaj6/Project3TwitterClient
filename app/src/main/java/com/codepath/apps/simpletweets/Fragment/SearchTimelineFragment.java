package com.codepath.apps.simpletweets.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.simpletweets.TwitterApplication;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by reneewu on 3/7/2017.
 */

public class SearchTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long currentMaxId;
    private int currentPage=0;

    public static SearchTimelineFragment newInstance(String query){
        SearchTimelineFragment f = new SearchTimelineFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        Log.d("debug","SearchTimelineFragment::OnCreate");
        populateTimeline(0);
    }

    @Override
    public void loadNextDataFromApi(int offset) {
        if (offset > currentPage)
            populateTimeline(currentMaxId);

        if(offset==0)
            populateTimeline(0);

        currentPage = offset;
    }

    public void populateTimeline(final long maxId) {

        String q = getArguments().getString("query");

        client.searchTweets(q, maxId, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("SearchTimelineFragment","populateTimeline onSuccess");
                Log.d("debug",json.toString());
                if(maxId==0)
                    tweets.clear();

                ArrayList<Tweet> newT = null;
                try {
                    newT = Tweet.fromJSONArray(json.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(newT.size()>0) {
                    currentMaxId = newT.get(newT.size() - 1).getUid();

                    tweets.addAll(newT);
                    aTweets.notifyDataSetChanged();
                }

                swipeContainer.setRefreshing(false);
                Log.d("debug","SearchTimelineFragment::populateTimeline Success");
            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug",errorResponse.toString());
            }
        });
    }

    public void addPost(Tweet newT){
        tweets.add(0, newT);
        aTweets.notifyItemInserted(0);

        // scrollToTop
        linearLayoutManager.scrollToPosition(0);
    }
}
