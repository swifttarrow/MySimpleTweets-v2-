package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeTweetActivity extends AppCompatActivity {
    private static final int CHARACTER_MAX = 140;
    public static int REQUEST_CODE = 20;
    private TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        client = TwitterApplication.getRestClient();

        Intent intent = getIntent();
        final TextView tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        tvCharCount.setText(String.valueOf(CHARACTER_MAX));
        EditText etNewTweet = (EditText) findViewById(R.id.etNewTweet);
        etNewTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                tvCharCount.setText(String.valueOf(CHARACTER_MAX-editable.length()));
            }
        });
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        Glide.with(getApplicationContext()).load(intent.getStringExtra(TimelineActivity.PARAM_PROFILE_URL)).into(ivProfileImage);
    }

    public void onSubmit(View v) {
        EditText etNewTweet = (EditText) findViewById(R.id.etNewTweet);
        String tweet =  etNewTweet.getText().toString();
        postTweet(tweet);
    }

    public void postTweet(String tweet){
        client.postTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());

                Intent data = new Intent();
                data.putExtra("tweet", Parcels.wrap(Tweet.fromJSON(json)));
                data.putExtra("code", REQUEST_CODE);

                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, tweet);
    }


}
