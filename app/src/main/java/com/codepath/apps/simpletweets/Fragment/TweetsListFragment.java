package com.codepath.apps.simpletweets.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.simpletweets.DetailsActivity;
import com.codepath.apps.simpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.adapter.TweetsAdapter;
import com.codepath.apps.simpletweets.helper.ItemClickSupport;
import com.codepath.apps.simpletweets.models.Tweet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reneewu on 3/7/2017.
 */

public class TweetsListFragment extends Fragment {
    //inflation logic

    public ArrayList<Tweet> tweets;
    public TweetsAdapter aTweets;
    public RecyclerView lvTweets;
    public EndlessRecyclerViewScrollListener scrollListener;
    private long currentMaxId;
    private long currentLargestMaxId;
    private int currentPage=0;
    public SwipeRefreshLayout swipeContainer;
    public LinearLayoutManager linearLayoutManager;
    // Store reference to the progress bar later
    ProgressBar progressBarFooter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container,false);

        // Lookup the recyclerview in activity layout
        lvTweets = (RecyclerView) v.findViewById(R.id.lvTweets);

        ItemClickSupport.addTo(lvTweets).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                                                    @Override
                                                                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                                                        Tweet tweet = tweets.get(position);

                                                                        Intent intent = new Intent(getActivity().getApplication(), DetailsActivity.class);
                                                                        intent.putExtra("tweet", Parcels.wrap(tweet));
                                                                        startActivity(intent);
                                                                    }
                                                                }
        );

        // Set the adapter AFTER adding footer
        lvTweets.setAdapter(aTweets);

        // Set layout manager to position the items
        lvTweets.setLayoutManager(linearLayoutManager);

        // Adds the scroll listener to RecyclerView
        lvTweets.addOnScrollListener(scrollListener);

        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                loadNextDataFromApi(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        aTweets = new TweetsAdapter(getActivity(), tweets);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
    }

    public void addAll(List<Tweet> tweets){
        tweets.addAll(tweets);
        aTweets.notifyDataSetChanged();
    }

    public void loadNextDataFromApi(int page){}
}
