package com.codepath.apps.simpletweets.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;

/**
 * Created by reneewu on 3/5/2017.
 */

public class ImageTextTweetViewHolder  extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    public TextView tvName;
    public TextView tvScreenName;
    public TextView tvBody;
    public TextView tvCreatedAt;
    public ImageView lvIamge;
    public ImageView ivBodyimage;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ImageTextTweetViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
        tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
        lvIamge = (ImageView) itemView.findViewById(R.id.imageView);
        ivBodyimage = (ImageView) itemView.findViewById(R.id.ivBodyimage);

    }
}