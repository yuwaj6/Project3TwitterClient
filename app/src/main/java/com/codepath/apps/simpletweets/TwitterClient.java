package com.codepath.apps.simpletweets;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "GBLVkRzV8iwlo38qGTOqbtUZQ";       // Change this
	public static final String REST_CONSUMER_SECRET = "wX85FqCFJThF1sHEoTLwcekmMGRjWZTTRnfKiafjdlsliZIxrW"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	//method - endpoint
	//home timeline
	public void getHomeTimeLine(long maxId, AsyncHttpResponseHandler handler) {
	    String apiUrl = getApiUrl("statuses/home_timeline.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("count",25);
		params.put("since_id",1);

		// max_id
		if(maxId!=0)
			params.put("max_id",maxId-1);

		getClient().get(apiUrl,params,handler);
	}

	public void getMentionsTimeLine(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("count",25);

		// max_id
		if(maxId!=0)
			params.put("max_id",maxId-1);

		getClient().get(apiUrl,params,handler);
	}

	public void getUserTimeLine(String screenName, long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("count",25);
		params.put("since_id",1);
		params.put("screen_name",screenName);

		// max_id
		if(maxId!=0)
			params.put("max_id",maxId-1);

		getClient().get(apiUrl,params,handler);
	}

    public void searchTweets(String q,long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");

        // Params
        RequestParams params = new RequestParams();
        params.put("q",q);
        params.put("count",25);
        params.put("since_id",1);

        // max_id
        if(maxId!=0)
            params.put("max_id",maxId-1);

        getClient().get(apiUrl,params,handler);
    }

	public void getCurrentUserInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");

		getClient().get(apiUrl,handler);
	}

	public void getUserInfo(Long UserId, String screen_name, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");

		RequestParams params = new RequestParams();
		params.put("user_id",UserId);
		params.put("screen_name",screen_name);

		getClient().get(apiUrl,params,handler);
	}

	//compose tweet
	// statuses/update.json
	// status
	public void postUpdate(String post, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("status",post);

		getClient().post(apiUrl,params,handler);
	}

	public void postUpdate(String post, Long replyToId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("status",post);
		params.put("in_reply_to_status_id",replyToId);

		getClient().post(apiUrl,params,handler);
	}

	//POST favorites/create
	public void favoritePost(Long postId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/create.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("id",postId);

		getClient().post(apiUrl,params,handler);
	}

	//POST favorites/destroy
	public void unfavoritePost(Long postId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("favorites/destroy.json");

		// Params
		RequestParams params = new RequestParams();
		params.put("id",postId);

		getClient().post(apiUrl,params,handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
