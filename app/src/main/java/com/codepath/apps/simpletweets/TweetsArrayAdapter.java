package com.codepath.apps.simpletweets;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codepath.apps.simpletweets.models.Tweet;

import java.util.List;

/**
 * Created by reneewu on 3/3/2017.
 */

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1,tweets);
    }
}
