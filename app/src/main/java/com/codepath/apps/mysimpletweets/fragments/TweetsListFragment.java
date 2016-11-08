package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.TweetDetailActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.misc.ItemClickSupport;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.codepath.apps.mysimpletweets.activities.TweetDetailActivity.REQUEST_CODE;

/**
 * Created by swifttarrow on 11/6/2016.
 */

public abstract class TweetsListFragment extends Fragment {
    protected TwitterClient client;

    protected RecyclerView rvTweets;
    protected ArrayList<Tweet> tweets;
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected SwipeRefreshLayout swipeContainer;
    protected LinearLayoutManager linearLayoutManager;
    protected TweetsAdapter adapter;

    protected long maxId;
    protected long sinceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.srLayout);
        setup();
        refresh(); //initial load of timeline.
        return v;
    }

    protected void setup(){
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(tweets, getActivity());
        configureSwipeOnRefreshLayout();
        rvTweets.setAdapter(adapter);
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                            Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                            intent.putExtra("tweet", Parcels.wrap(tweets.get(position)));
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }
                }
        );
        configureEndlessScrolling();
    }

    private void configureSwipeOnRefreshLayout(){
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeContainer.setRefreshing(false);

            }
        });
    }

    private void configureEndlessScrolling(){
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rvTweets.setLayoutManager(linearLayoutManager);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                maxId = tweets.get(tweets.size()-1).getUid();
                populate(sinceId, maxId);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

    protected void populate(){
        populate(0, 0);
    }
    protected abstract void populate(long sinceId, long maxId);

    protected void refresh(){
        adapter.clear();
        populate(0, 0);
    }

    public void add(Tweet tweet){
        adapter.add(tweet);
        maxId = tweets.get(tweets.size()-1).getUid();
    }
    public void addAll(List<Tweet> tweets){
        adapter.add(tweets);
        sinceId = tweets.get(0).getUid();
        maxId = tweets.get(tweets.size()-1).getUid();
    }

    public String getProfileImageURL(){
        if (tweets.size() > 0){
            return tweets.get(0).getUser().getProfileImageUrl();
        }
        return null;
    }

    public LinearLayoutManager getLayoutManager(){
        return linearLayoutManager;
    }
}
