package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {
    public static String PARAM_PROFILE_URL = "profile_image_url";

    private SmartFragmentStatePagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);

        //fab
        configureFABCompose();

        //viewpager + tabs
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.miProfile:
                onProfileView(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem mi){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
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

    public void startComposeActivity(){
        HomeTimelineFragment fragment = (HomeTimelineFragment)adapterViewPager.getRegisteredFragment(0);
        Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("profile_image_url", fragment.getProfileImageURL());
        startActivityForResult(i, ComposeTweetActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ComposeTweetActivity.REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable("tweet"));
                addTweetToHomeTimeline(tweet);
            }
        } else if (requestCode == TweetDetailActivity.REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Tweet tweet = Parcels.unwrap(data.getExtras().getParcelable("tweet"));
                addTweetToHomeTimeline(tweet);
                if (true){
                    addTweetToMentionsTimeline(tweet);
                }
            }
        }
    }

    private void addTweetToHomeTimeline(Tweet tweet){
        HomeTimelineFragment fragment = (HomeTimelineFragment)adapterViewPager.getRegisteredFragment(0);
        fragment.add(tweet);
        fragment.getLayoutManager().scrollToPositionWithOffset(0, 0);
    }

    private void addTweetToMentionsTimeline(Tweet tweet){
        MentionsTimelineFragment fragment = (MentionsTimelineFragment)adapterViewPager.getRegisteredFragment(1);
        fragment.add(tweet);
        fragment.getLayoutManager().scrollToPositionWithOffset(0, 0);
    }
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return new HomeTimelineFragment();
            } else if (position == 1){
                return new MentionsTimelineFragment();
            } else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
