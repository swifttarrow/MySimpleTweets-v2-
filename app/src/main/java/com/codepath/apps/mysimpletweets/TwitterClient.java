package com.codepath.apps.mysimpletweets;

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
	public static final String REST_CONSUMER_KEY = "3YiRJOY6tGegBUlBXja54xWeJ";       // Change this
	public static final String REST_CONSUMER_SECRET = "h3yGIi0KL6lwWTgE8gHvuAZbTwitODUesE83JumcuC3GlxgUV6"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)
	public static final String PARAM_COUNT = "count";
	public static final int DEFAULT_COUNT_VALUE = 25;
	public static final String PARAM_SINCE_ID = "since_id";
	public static final String PARAM_STATUS = "status";
	public static final String PARAM_REPLY_TO = "in_reply_to_status_id";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler){
		getHomeTimeline(handler, 1);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler, int page){
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		RequestParams params = new RequestParams();
		params.put(PARAM_COUNT, DEFAULT_COUNT_VALUE);
		params.put(PARAM_SINCE_ID, ((page-1)* DEFAULT_COUNT_VALUE) + 1);
		getClient().get(apiUrl, params, handler);
	}

	public void postTweet(AsyncHttpResponseHandler handler, String tweet){
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put(PARAM_STATUS, tweet);
		getClient().post(apiUrl, params, handler);
	}

	public void postReplyTweet(AsyncHttpResponseHandler handler, String tweet, String replyUser){
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put(PARAM_STATUS, tweet);
		params.put(PARAM_REPLY_TO, replyUser);
		getClient().post(apiUrl, params, handler);
	}

}
