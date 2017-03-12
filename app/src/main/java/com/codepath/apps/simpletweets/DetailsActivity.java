package com.codepath.apps.simpletweets;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.Fragment.ComposeFragment;
import com.codepath.apps.simpletweets.models.Media;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.utils.PatternEditableBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class DetailsActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListner  {
    public TextView tvName;
    public TextView tvScreenName;
    public TextView tvBody;
    public TextView tvCreatedAt;
    public ImageView lvIamge;
    private TwitterClient client;
    private Tweet t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        client = TwitterApplication.getRestClient();

        t = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        lvIamge = (ImageView) findViewById(R.id.imageView);

        // Set item views based on your views and data model
        tvName.setText(t.getUser().getName());
        tvScreenName.setText(t.getUser().getScreenName());
        tvBody.setText(t.getBody());
        // Style clickable spans based on pattern
        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.parseColor("#00aced"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Intent i = new Intent(getBaseContext(), ProfileActivity.class);
                                i.putExtra("screen_name", text.substring(1));
                                getBaseContext().startActivity(i);
                            }
                        }).
                addPattern(Pattern.compile("\\#(\\w+)"), Color.parseColor("#00aced"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(getBaseContext(), "Clicked hashtag: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(tvBody);

        tvCreatedAt.setText(t.getCreatedAt());
        Glide.with(this)
                .load(t.getUser().getProfileImageUrl())
                .into(lvIamge);

        if(t.getEntities().getMedia()!=null){
            ImageView ivBodyImg = (ImageView) findViewById(R.id.ivBodyImage);
            Glide.with(this)
                    .load(Media.getPhotoMedia(t.getEntities().getMedia()).getMediaUrl())
                    .into(ivBodyImg);
        }

        ImageView ivReply = (ImageView) findViewById(R.id.ivReply);
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComposeDialog(t.getUser().getScreenDisplayName(),t.getUid());
            }
        });

        final ImageView ivFav = (ImageView) findViewById(R.id.ivfavorite);
        // change color if favorited
        if(t.getFavorited())
            ivFav.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_favorited));
        else
            ivFav.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_favorite));
        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean favorited = favOrUnfavoritePost();

                if(favorited)
                    ivFav.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.ic_favorited));
                else
                    ivFav.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.ic_favorite));
            }
        });
    }

    private Boolean favOrUnfavoritePost(){
        TwitterClient client = TwitterApplication.getRestClient();
        //final Tweet t = mTweets.get(position);
        Long postId = t.getUid();
        Boolean currentFavStatus = t.getFavorited();

        if(currentFavStatus){
            client.unfavoritePost(postId,new JsonHttpResponseHandler(){
                // success

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.d("unfavoritePost","onSuccess");
                    t = Tweet.fromJSON(json);
                }

                // failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("debug",errorResponse.toString());
                }
            });

            return false;
        }
        else{
            client.favoritePost(postId,new JsonHttpResponseHandler(){
                // success
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                    Log.d("favoritePost","onSuccess");
                    t = Tweet.fromJSON(json);
                }

                // failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("debug",errorResponse.toString());
                }
            });

            return true;
        }
    }

    private void showComposeDialog(String prefill, long replyToId) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(prefill, replyToId);
        composeFragment.show(fm, "fragment_compose");
    }

    @Override
    public void onFinishComposeDialog(String post) {
        // do nothing
    }

    public void onFinishComposeDialog(String post, Long replyToId) {
        client.postUpdate(post,replyToId, new JsonHttpResponseHandler(){
            // success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("debug","reply success");
            }

            // failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("debug",errorResponse.toString());
            }
        });
    }
}
