package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.views.LinkifiedTextView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {
    public static int REQUEST_CODE = 30;
    public Tweet parentTweet;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        setupToolbar();

        client = TwitterApplication.getRestClient();

        parentTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        LinkifiedTextView tvBody = (LinkifiedTextView) findViewById(R.id.tvBody);

        tvName.setText(parentTweet.getUser().getName());
        tvUsername.setText(parentTweet.getUser().getScreenName());
        tvCreatedAt.setText(parentTweet.getCreatedAt());
        tvBody.setText(parentTweet.getBody());

        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getApplicationContext()).load(parentTweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        ivProfileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TweetDetailActivity.this, ProfileActivity.class);
                i.putExtra("user", Parcels.wrap(parentTweet.getUser()));
                startActivity(i);
            }
        });

        EditText etReply = (EditText) findViewById(R.id.etReply);
        etReply.setHint(getResources().getString(R.string.reply_to_hint) + " " + parentTweet.getUser().getName());

        etReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etView = (EditText) view;
                if (etView.getText().length() == 0){
                    etView.setText(parentTweet.getUser().getScreenName());
                    etView.setSelection(etView.getText().length());
                }
            }
        });
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);
    }

    public void onSubmit(View v) {
        EditText etReply = (EditText) findViewById(R.id.etReply);

        if (etReply.length() == 0){
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.empty_tweet_error), Toast.LENGTH_SHORT).show();
            return;
        }
        String replyTweet = etReply.getText().toString();
        String replyUser = parentTweet.getUser().getScreenName();
        postReplyTweet(replyTweet, replyUser);
    }

    public void postReplyTweet(String tweetText, String replyUser){
        client.postReplyTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Intent data = new Intent();

                data.putExtra("tweet", Parcels.wrap(Tweet.fromJSON(json)));
                data.putExtra("code", REQUEST_CODE); // ints work too
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, tweetText, replyUser);
    }
}
