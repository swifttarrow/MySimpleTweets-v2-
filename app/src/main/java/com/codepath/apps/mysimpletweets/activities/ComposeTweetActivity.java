package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;

public class ComposeTweetActivity extends AppCompatActivity {
    private static final int CHARACTER_MAX = 140;
    public static int REQUEST_CODE = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

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
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("tweet", etNewTweet.getText().toString());
        data.putExtra("code", REQUEST_CODE); // ints work too
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
