package com.example.oya.newsreader.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.oya.newsreader.model.NewsArticle;

import java.util.List;

public class SearchLoader extends AsyncTaskLoader<List<NewsArticle>> {

    /** Tag for log messages */
    private static final String LOG_TAG = SearchLoader.class.getName();

    /** Query URL */
    private String mQuery;

    /**
     * Constructs a new {@link AllSectionsLoader}.
     *
     * @param section to load data from
     */
    public SearchLoader(Context context, String section) {
        super(context);
        mQuery = section;
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
        if (mQuery == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<NewsArticle> articles = NetworkUtils.searchOnline(mQuery);
        return articles;
    }

}
