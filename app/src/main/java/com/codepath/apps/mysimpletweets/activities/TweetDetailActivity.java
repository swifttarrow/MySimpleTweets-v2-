package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.views.LinkifiedTextView;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {
    public static int REQUEST_CODE = 30;
    public Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        setupToolbar();

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        LinkifiedTextView tvBody = (LinkifiedTextView) findViewById(R.id.tvBody);

        tvName.setText(tweet.getUser().getName());
        tvUsername.setText(tweet.getUser().getScreenName());
        tvCreatedAt.setText(tweet.getCreatedAt());
        tvBody.setText(tweet.getBody());

        ivProfileImage.setImageResource(android.R.color.transparent);
        Glide.with(getApplicationContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        EditText etReply = (EditText) findViewById(R.id.etReply);
        etReply.setHint(getResources().getString(R.string.reply_to_hint) + " " + tweet.getUser().getName());

        etReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etView = (EditText) view;
                if (etView.getText().length() == 0){
                    etView.setText(tweet.getUser().getScreenName());
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
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.empty_tweet_error), Toast.LENGTH_SHORT);
            return;
        }
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("tweet", etReply.getText().toString());
        data.putExtra("replyUser", tweet.getUser().getScreenName());
        data.putExtra("code", REQUEST_CODE); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
