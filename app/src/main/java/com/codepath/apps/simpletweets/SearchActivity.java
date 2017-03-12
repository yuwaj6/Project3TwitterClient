package com.codepath.apps.simpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.simpletweets.Fragment.SearchTimelineFragment;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String query = getIntent().getStringExtra("query");
        SearchTimelineFragment searchTimelineFragment = SearchTimelineFragment.newInstance(query);

        // display
        if(savedInstanceState==null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer,searchTimelineFragment);
            ft.commit(); // changes the fragment
        }
    }
}
