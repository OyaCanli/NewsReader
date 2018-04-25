package com.example.oya.newsreader.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.oya.newsreader.model.NewsArticle;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mSection;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param section to load data from
     */
    public NewsLoader(Context context, String section) {
        super(context);
        mSection = section;
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
        List<NewsArticle> articles = NetworkUtils.fetchArticles(mSection);
        for(int i=0; i<articles.size(); i++){
            Log.d("NEEWSLOADER", ""+ articles.get(i).toString());
        }
        return articles;
    }

}
