package com.example.oya.newsreader.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;

import java.util.List;

public class SectionLoader extends AsyncTaskLoader<List<NewsArticle>> {

    private final String mSection;
    private List<NewsArticle> mArticles;

    /**
     * Constructs a new {@link SectionLoader}.
     *
     * @param context of the activity
     * @param section to load data from
     */
    public SectionLoader(Context context, String section) {
        super(context);
        mSection = section;
    }

    @Override
    protected void onStartLoading() {
        if (mArticles != null) {
            // Use cached data
            deliverResult(mArticles);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }
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
        List<NewsArticle> articles = NetworkUtils.fetchArticles(mSection, getContext());
        NewsDbHelper dbHelper = new NewsDbHelper(getContext(), mSection);
        dbHelper.backUpToDatabase(articles);
        return articles;
    }

    @Override
    public void deliverResult(List<NewsArticle> articles) {
        // We’ll save the data for later retrieval
        mArticles = articles;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(mArticles);
    }
}