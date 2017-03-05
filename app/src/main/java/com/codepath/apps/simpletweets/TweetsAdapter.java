package com.codepath.apps.simpletweets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweets.models.Media;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.viewHolder.ImageTextTweetViewHolder;
import com.codepath.apps.simpletweets.viewHolder.textOnlyTweetViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.codepath.apps.simpletweets.models.Media.getPhotoMedia;

/**
 * Created by reneewu on 3/4/2017.
 */

public class TweetsAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Store a member variable for the contacts
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;
    private final int TEXTONLY = 0, WITHIMAGE = 1;

    // Pass in the contact array into the constructor
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access

    @Override
    public int getItemViewType(int position) {
        if (Media.getPhotoMedia(mTweets.get(position).getEntities().getMedia())!=null) {
            return WITHIMAGE;
        } else {
            return TEXTONLY;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case WITHIMAGE:
                View v1 = inflater.inflate(R.layout.item_tweet_image, parent, false);
                viewHolder = new ImageTextTweetViewHolder(v1);
                break;
            case TEXTONLY:
                View v2 = inflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new textOnlyTweetViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new textOnlyTweetViewHolder(v3);
                break;
        }
        return viewHolder;
    }

    private void ConfigureTextOnlyTweetViewHolder(textOnlyTweetViewHolder viewHolder, int position){
        Tweet t = mTweets.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.tvName;
        textView.setText(t.getUser().getName());

        TextView tvScreenName = viewHolder.tvScreenName;
        tvScreenName.setText(t.getUser().getScreenName());

        TextView tvBody = viewHolder.tvBody;
        tvBody.setText(t.getBody());

        TextView tvCreatedAt = viewHolder.tvCreatedAt;
        try {
            tvCreatedAt.setText(getRelativeTimeAgo(t.getCreatedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ImageView ivImg = viewHolder.lvIamge;

        Glide.with(getContext())
                .load(t.getUser().getProfileImageUrl())
                .into(ivImg);
    }

    private void ConfigureImageTextTweetViewHolder(ImageTextTweetViewHolder viewHolder, int position){
        Tweet t = mTweets.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.tvName;
        textView.setText(t.getUser().getName());

        TextView tvScreenName = viewHolder.tvScreenName;
        tvScreenName.setText(t.getUser().getScreenName());

        TextView tvBody = viewHolder.tvBody;
        tvBody.setText(t.getBody());

        TextView tvCreatedAt = viewHolder.tvCreatedAt;
        try {
            tvCreatedAt.setText(getRelativeTimeAgo(t.getCreatedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ImageView ivImg = viewHolder.lvIamge;

        Glide.with(getContext())
                .load(t.getUser().getProfileImageUrl())
                .into(ivImg);

        ImageView ivBodyImg = viewHolder.ivBodyimage;
        Glide.with(getContext())
                .load(getPhotoMedia(mTweets.get(position).getEntities().getMedia()).getMediaUrl())
                .into(ivBodyImg);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case TEXTONLY:
                textOnlyTweetViewHolder vh1 = (textOnlyTweetViewHolder) viewHolder;
                ConfigureTextOnlyTweetViewHolder(vh1, position);
                break;
            case WITHIMAGE:
                ImageTextTweetViewHolder vh2 = (ImageTextTweetViewHolder) viewHolder;
                ConfigureImageTextTweetViewHolder(vh2, position);
                break;
            default:
                textOnlyTweetViewHolder vh = (textOnlyTweetViewHolder) viewHolder;
                ConfigureTextOnlyTweetViewHolder(vh, position);
                break;
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) throws ParseException {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}