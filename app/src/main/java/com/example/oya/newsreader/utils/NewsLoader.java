package com.example.oya.newsreader.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<Object> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /**
     * Query URL
     */
    private List<String> mSectionList;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param sections to load data from
     */
    public NewsLoader(Context context, List<String> sections) {
        super(context);
        mSectionList = sections;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public Object loadInBackground() {
        if (mSectionList == null) {
            return null;
        }
        for(int i = 0; i < mSectionList.size(); ++i){
            List<NewsArticle> articles = NetworkUtils.fetchArticles(mSectionList.get(i), getContext());
            NewsDbHelper dbHelper = new NewsDbHelper(getContext(), mSectionList.get(i));
            dbHelper.backUpToDatabase(articles);
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        return null;
    }

}
