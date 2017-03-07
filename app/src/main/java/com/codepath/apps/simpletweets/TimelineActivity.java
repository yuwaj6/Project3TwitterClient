package com.codepath.apps.simpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletweets.Fragment.ComposeFragment;
import com.codepath.apps.simpletweets.Fragment.HomeTimelineFragment;
import com.codepath.apps.simpletweets.Fragment.MentionTimelineFragment;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

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

        FloatingActionButton btnAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComposeDialog("");
            }
        });

        ViewPager vp =(ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // todo: refresh home timeline
    }

    private void showComposeDialog(String prefill) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(prefill);
        composeFragment.show(fm, "fragment_compose");
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

    // return the order of fragments
    public class TweetsPagerAdapter extends FragmentPagerAdapter{

        private String tabTitles[] = {"Home","Mentions"};

        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return new HomeTimelineFragment();
            else if(position==1)
                return new MentionTimelineFragment();
            else
                return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
