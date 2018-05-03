package com.example.oya.newsreader.data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.oya.newsreader.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public final class DatabaseUtils {

    private static final int BACKUP_INTERVAL_HOURS = 6;
    private static final int BACKUP_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(BACKUP_INTERVAL_HOURS));
    private static final int SYNCH_FLEXTIME_SECONDS = (int) (TimeUnit.HOURS.toSeconds(1));

    private static final String REMINDER_JOB_TAG = "news_backup_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleNewsBackUp(@NonNull final Context context){
        if(sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job newsCheckerJob = dispatcher.newJobBuilder()
                .setService(NewsBackUpService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.DEVICE_IDLE, Constraint.ON_UNMETERED_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(BACKUP_INTERVAL_SECONDS, BACKUP_INTERVAL_SECONDS + SYNCH_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(newsCheckerJob);
        sInitialized = true;
    }

    public static void testNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel("channel", "New Article Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channel")
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("test")
                .setContentText("database is backed up")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(4251, notificationBuilder.build());

    }
}
