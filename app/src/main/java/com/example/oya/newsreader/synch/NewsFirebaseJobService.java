package com.example.oya.newsreader.synch;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class NewsFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchNewsTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchNewsTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                SyncTask.syncNewsDatabase(getApplicationContext());
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };
        mFetchNewsTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFetchNewsTask != null) mFetchNewsTask.cancel(true);
        return true;
    }
}
