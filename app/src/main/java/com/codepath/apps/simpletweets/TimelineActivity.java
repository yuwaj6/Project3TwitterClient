package com.codepath.apps.simpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletweets.helper.ItemClickSupport;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListner {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter aTweets;
    //private ListView lvTweets;
    private RecyclerView lvTweets;
    private EndlessRecyclerViewScrollListener scrollListener;
    private long currentMaxId;
    private long currentLargestMaxId;
    private int currentPage=0;
    private SwipeRefreshLayout swipeContainer;
    private User currentUser;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Lookup the recyclerview in activity layout
        lvTweets = (RecyclerView) findViewById(R.id.lvTweets);

        ItemClickSupport.addTo(lvTweets).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                                                    @Override
                                                                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                                                        Tweet tweet = tweets.get(position);

                                                                        Intent intent = new Intent(getApplication(), DetailsActivity.class);
                                                                        intent.putExtra("tweet", Parcels.wrap(tweet));
                                                                        startActivity(intent);
                                                                    }
                                                                }
        );

        //lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);
        linearLayoutManager = new LinearLayoutManager(this);

        // Set layout manager to position the items
        lvTweets.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        lvTweets.addOnScrollListener(scrollListener);

        client = TwitterApplication.getRestClient();
        getCurrentUser();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String titleOfPage = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                String urlOfPage = intent.getStringExtra(Intent.EXTRA_TEXT);

                showComposeDialog(titleOfPage + "\n" + urlOfPage);
            }
        }

        populateTimeline(0);

        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComposeDialog("");
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                populateTimeline(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void showComposeDialog(String prefill) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(prefill);
        composeFragment.show(fm, "fragment_compose");
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        if (offset > currentPage)
            populateTimeline(currentMaxId);
        //else
        //    populateTimeline(currentLargestMaxId);

        currentPage = offset;
    }

    // send an API request to get the timeline json
    // Fill the listview by creating the twwt objects from the json
    private void populateTimeline(final long maxId) {
        client.getHomeTimeLine(maxId, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("debug",json.toString());
                if(maxId==0)
                    tweets.clear();

                ArrayList<Tweet> newT = Tweet.fromJSONArray(json);
                currentMaxId = newT.get(newT.size()-1).getUid();
                currentLargestMaxId = newT.get(0).getUid();
                tweets.addAll(newT);
                aTweets.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug",errorResponse.toString());
            }
        });
    }

    private void getCurrentUser(){
        client.getCurrentUserInfo(new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("debug",json.toString());

                currentUser = User.fromJSON(json);
                Utils.profileImageUrl = currentUser.getProfileImageUrl();
            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug",errorResponse.toString());
            }
        });
    }

    @Override
    public void onFinishComposeDialog(String post) {
        client.postUpdate(post, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("debug",json.toString());

                Tweet newT = Tweet.fromJSON(json);
                //currentMaxId = newT.get(newT.size()-1).getUid();
                //currentLargestMaxId = newT.get(0).getUid();
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

    public void onFinishComposeDialog(String post, Long replyToId) {
        client.postUpdate(post,replyToId, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("debug",json.toString());

                Tweet newT = Tweet.fromJSON(json);
                //currentMaxId = newT.get(newT.size()-1).getUid();
                //currentLargestMaxId = newT.get(0).getUid();
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
