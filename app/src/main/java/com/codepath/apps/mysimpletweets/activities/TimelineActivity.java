package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.misc.ItemClickSupport;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.activities.ComposeTweetActivity.REQUEST_CODE;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private RecyclerView rvTweets;
    private EndlessRecyclerViewScrollListener scrollListener;
    private SwipeRefreshLayout swipeContainer;
    private LinearLayoutManager linearLayoutManager;

    public static String PARAM_PROFILE_URL = "profile_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setup();
        populateTimeline();
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void configureSwipeOnRefreshLayout(){
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.srLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
                swipeContainer.setRefreshing(false);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void startComposeActivity(){
        Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
        Bundle bundle = new Bundle();
        if (tweets.size() > 0){
            bundle.putString("profile_image_url", tweets.get(0).getUser().getProfileImageUrl());
        }
        startActivityForResult(i, ComposeTweetActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ComposeTweetActivity.REQUEST_CODE){
            if (resultCode == RESULT_OK){
                postTweet(data.getExtras().getString("tweet"));
            }
        } else if (requestCode == TweetDetailActivity.REQUEST_CODE){
            if (resultCode == RESULT_OK){
                postReplyTweet(data.getExtras().getString("tweet"), data.getExtras().getString("replyUser"));
            }
        }
    }

    private void postTweet(String tweet){
        client.postTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                TweetsAdapter tweetsAdapter = (TweetsAdapter) (rvTweets.getAdapter());
                tweetsAdapter.add(Tweet.fromJSON(json));
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, tweet);
    }

    private void postReplyTweet(String tweet, String replyUser){
        client.postReplyTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                TweetsAdapter tweetsAdapter = (TweetsAdapter) (rvTweets.getAdapter());
                tweetsAdapter.add(Tweet.fromJSON(json));
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, tweet, replyUser);
    }

    private void setup(){
        setupToolbar();
        configureSwipeOnRefreshLayout();
        configureFABCompose();
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        rvTweets.setAdapter(new TweetsAdapter(tweets, this));
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                            Intent intent = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                            intent.putExtra("tweet", Parcels.wrap(tweets.get(position)));
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }
                }
        );
        client = TwitterApplication.getRestClient();
        configureEndlessScrolling();
    }

    private void configureFABCompose(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCompose);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startComposeActivity();
            }
        });
    }

    private void configureEndlessScrolling(){
        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void loadNextDataFromApi(int page){
        populateTimeline(page);
    }

    private void refreshTimeline() {
        TweetsAdapter tweetsAdapter = (TweetsAdapter) (rvTweets.getAdapter());
        tweetsAdapter.clear();
        populateTimeline();
    }
    private void populateTimeline() {
        populateTimeline(1);
    }

    private void populateTimeline(int page){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                TweetsAdapter tweetsAdapter = (TweetsAdapter) (rvTweets.getAdapter());
                tweetsAdapter.add(Tweet.fromJSONArray(json));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, page);
    }

}
