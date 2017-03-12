package com.codepath.apps.simpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.Fragment.ComposeFragment;
import com.codepath.apps.simpletweets.models.Media;
import com.codepath.apps.simpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class DetailsActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListner  {
    public TextView tvName;
    public TextView tvScreenName;
    public TextView tvBody;
    public TextView tvCreatedAt;
    public ImageView lvIamge;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        client = TwitterApplication.getRestClient();

        final Tweet t = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        lvIamge = (ImageView) findViewById(R.id.imageView);

        // Set item views based on your views and data model
        tvName.setText(t.getUser().getName());
        tvScreenName.setText(t.getUser().getScreenName());
        tvBody.setText(t.getBody());
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
