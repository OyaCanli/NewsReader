package com.example.oya.newsreader.synch;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.oya.newsreader.utils.Constants;

public class SyncIntentService extends IntentService {

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncTask.syncNewsDatabase(this);
        sendBroadCastToMain();
    }

    private void sendBroadCastToMain(){
        Intent intent = new Intent();
        intent.setAction(Constants.BROADCAST_ACTION);
        sendBroadcast(intent);
    }
}
