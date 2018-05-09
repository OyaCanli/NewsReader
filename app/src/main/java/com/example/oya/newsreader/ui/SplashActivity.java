package com.example.oya.newsreader.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.utils.Constants;
import com.example.oya.newsreader.utils.NewsLoader;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object>, Animation.AnimationListener {

    ArrayList<String> sectionList;
    int viewCount;
    View v1, v2, v3, v4;
    Animation translate_1, translate_2, translate_3, translate_4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView tv = findViewById(R.id.splash_title);
        Animation fade_animation= AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_anim);
        tv.startAnimation(fade_animation);
        v1 = findViewById(R.id.view_fromLeft);
        v2 = findViewById(R.id.view_fromBottom);
        v3 = findViewById(R.id.view_fromRight);
        v4 = findViewById(R.id.view_fromTop);
        translate_1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate_from_left);
        translate_2 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate_from_bottom);
        translate_3 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate_from_right);
        translate_4 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.translate_from_top);
        translate_1.setAnimationListener(this);
        translate_2.setAnimationListener(this);
        translate_3.setAnimationListener(this);
        translate_4.setAnimationListener(this);
        v1.startAnimation(translate_1);
        v1.setVisibility(View.VISIBLE);

        sectionList = SortSectionsActivity.getSections(this);
        if (thereIsConnection()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(Constants.SYNCH_AT_LAUNCH_LOADER_ID, null, this);
        }
    }

    private boolean thereIsConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public android.content.Loader<Object> onCreateLoader(int id, Bundle args) {
        return new NewsLoader(this, sectionList);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Object> loader, Object data) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        View[] viewsToAnimate = {v1, v2, v3, v4};
        Animation[] animations = {translate_1, translate_2, translate_3, translate_4};
        viewCount++;
        if(viewCount >=4) return;
        viewsToAnimate[viewCount].startAnimation(animations[viewCount]);
        viewsToAnimate[viewCount].setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Object> loader) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
