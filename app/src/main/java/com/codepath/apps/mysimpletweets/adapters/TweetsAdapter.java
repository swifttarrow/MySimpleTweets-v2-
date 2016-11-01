package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

/**
 * Created by swifttarrow on 10/30/2016.
 */

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    private Context mContext;

    public TweetsAdapter(List<Tweet> tweets, Context context){
        mTweets = tweets;
        mContext = context;
    }

    private Context getContext(){
        return mContext;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivProfileImage;
        public TextView tvName;
        public TextView tvUsername;
        public TextView tvCreatedAt;
        public TextView tvBody;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        }
    }
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Set item views based on your views and data model
        TextView tvName = viewHolder.tvName;
        TextView tvUsername = viewHolder.tvUsername;
        TextView tvCreatedAt = viewHolder.tvCreatedAt;
        TextView tvBody = viewHolder.tvBody;
        ImageView ivProfileImage = viewHolder.ivProfileImage;

        tvName.setText(tweet.getUser().getName());
        tvUsername.setText(tweet.getUser().getScreenName());
        tvCreatedAt.setText(tweet.getCreatedAt());
        tvBody.setText(tweet.getBody());
        /*tvBody.setMovementMethod(LinkMovementMethod.getInstance());*/
        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void add(List<Tweet> tweets){
        int insertPos = mTweets.size();
        mTweets.addAll(tweets);
        notifyItemRangeInserted(insertPos, tweets.size());
    }
    public void add(Tweet tweet){
        mTweets.add(0, tweet);
        notifyItemInserted(0);
    }
    public void clear(){
        int numItems = mTweets.size();
        mTweets.clear();
        notifyItemRangeRemoved(0, numItems);
    }
}
