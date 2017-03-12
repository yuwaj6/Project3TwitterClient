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
 * Created by reneewu on 3/7/2017.
 */

public class HomeTimelineFragment extends TweetsListFragment  implements ComposeFragment.ComposeFragmentListner{

    private TwitterClient client;
    private long currentMaxId;
    private int currentPage=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();

        Log.d("debug","Home Timeline Fragment");
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
        client.getHomeTimeLine(maxId, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("debug",json.toString());
                if(maxId==0)
                    tweets.clear();

                ArrayList<Tweet> newT = Tweet.fromJSONArray(json);
                if(newT.size()>0) {
                    currentMaxId = newT.get(newT.size() - 1).getUid();

                    tweets.addAll(newT);
                    aTweets.notifyDataSetChanged();
                }

                swipeContainer.setRefreshing(false);
                Log.d("debug","populateTimeline Success");
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

    @Override
    public void onFinishComposeDialog(String post) {
        client.postUpdate(post, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("debug",json.toString());

                Tweet newT = Tweet.fromJSON(json);

            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug",errorResponse.toString());
            }
        });
    }

    public void onFinishComposeDialog(String post, Long replyToId) {
        client.postUpdate(post,replyToId, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("debug",json.toString());

                Tweet newT = Tweet.fromJSON(json);
                tweets.add(0, newT);
                aTweets.notifyItemInserted(0);

                // scrollToTop
                linearLayoutManager.scrollToPosition(0);
            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug",errorResponse.toString());
            }
        });
    }
}
