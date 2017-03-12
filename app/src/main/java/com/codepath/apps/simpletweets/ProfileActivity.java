package com.codepath.apps.simpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.Fragment.UserTimelineFragment;
import com.codepath.apps.simpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    private TwitterClient client;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Long userId = getIntent().getLongExtra("user_id",0);
        String screenName = getIntent().getStringExtra("screen_name");
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

        // display
        if(savedInstanceState==null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer,userTimelineFragment);
            ft.commit(); // changes the fragment
        }

        client = TwitterApplication.getRestClient();

        if(userId!=0){
            client.getUserInfo(userId,screenName,new JsonHttpResponseHandler(){
                // success
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.d("debug",json.toString());

                    // update view
                    user = User.fromJSON(json);
                    getSupportActionBar().setTitle(user.getScreenDisplayName());
                    populateProfileHeader(user);
                }

                // failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("debug",errorResponse.toString());
                }
            });
        }
        else{
            client.getCurrentUserInfo(new JsonHttpResponseHandler(){
                // success
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.d("debug",json.toString());

                    // update view
                    user = User.fromJSON(json);
                    getSupportActionBar().setTitle(user.getScreenDisplayName());
                    populateProfileHeader(user);
                }

                // failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("debug",errorResponse.toString());
                }
            });
        }

    }

    private void populateProfileHeader(User user){
        TextView tvName= (TextView) findViewById(R.id.tvName);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowingCount);
        TextView tvFollower = (TextView) findViewById(R.id.tvFollowerCount);
        ImageView ivProfile =(ImageView) findViewById(R.id.imageView);

        tvName.setText(user.getName());
        tvScreenName.setText(user.getScreenDisplayName());
        tvFollowing.setText(user.getFollowingCount()+" Following");
        tvFollower.setText(user.getFollowersCount() + " Followers");
        Glide.with(getApplicationContext())
                .load(user.getProfileImageUrl())
                .into(ivProfile);
    }
}
