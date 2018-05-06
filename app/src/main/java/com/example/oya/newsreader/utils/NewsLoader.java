package com.example.oya.newsreader.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mSection;
    private Context mContext;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param section to load data from
     */
    public NewsLoader(Context context, String section) {
        super(context);
        mSection = section;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsArticle> loadInBackground() {
        if (mSection == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<NewsArticle> articles = NetworkUtils.fetchArticles(mSection, mContext);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if(preferences.getBoolean(mContext.getString(R.string.pref_key_offline_reading), mContext.getResources().getBoolean(R.bool.pref_offline_reading_default))){
            NewsDbHelper dbHelper = new NewsDbHelper(mContext, mSection);
            dbHelper.backUpToDatabase(articles);
        }
        return articles;
    }

}
