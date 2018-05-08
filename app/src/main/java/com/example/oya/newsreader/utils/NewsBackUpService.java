package com.example.oya.newsreader.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
                int size = sections.size();
                for(int i = 0; i < size; ++i){
                    List<NewsArticle> articlesToBeBacked = NetworkUtils.fetchArticles(sections.get(i), context);
                    NewsDbHelper dbHelper = new NewsDbHelper(context, sections.get(i));
                    dbHelper.backUpToDatabase(articlesToBeBacked);
                }
                DatabaseUtils.testNotification(context); //TODO. don't forget to erase this method later
                Log.v("NewsBackUpService", "copied to database");
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
