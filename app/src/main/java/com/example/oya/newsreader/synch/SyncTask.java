package com.example.oya.newsreader.synch;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.oya.newsreader.ui.SortSectionsActivity;
import com.example.oya.newsreader.utils.NetworkUtils;

import java.util.ArrayList;

import static com.example.oya.newsreader.data.NewsContract.BASE_CONTENT_URI;

public class SyncTask {

    synchronized public static void syncNewsDatabase(Context context) {

        try{
            ArrayList<String> sectionList = SortSectionsActivity.getSections(context);

            for(int i = 0; i < sectionList.size(); ++i){
                ContentValues[] articleContentValues = NetworkUtils.fetchArticles(sectionList.get(i), context);
                Uri content_uri = BASE_CONTENT_URI.buildUpon().appendPath(sectionList.get(i)).build();
                if(articleContentValues != null && articleContentValues.length != 0){
                    ContentResolver newsContentResolver = context.getContentResolver();
                    newsContentResolver.delete(content_uri, null, null);
                    newsContentResolver.bulkInsert(content_uri, articleContentValues);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, SyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
