package com.example.oya.newsreader.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.oya.newsreader.data.NewsDbHelper;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.ui.SortSectionsActivity;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;
import java.util.List;

public class NewsBackUpService extends JobService {

    private static AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = NewsBackUpService.this;
                ArrayList<String> sections = SortSectionsActivity.getSections(context);
                for(String section : sections){
                    List<NewsArticle> articlesToBeBacked = NetworkUtils.fetchArticles(section, context);
                    NewsDbHelper dbHelper = new NewsDbHelper(context, section);
                    dbHelper.backUpToDatabase(articlesToBeBacked);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
                super.onPostExecute(o);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
