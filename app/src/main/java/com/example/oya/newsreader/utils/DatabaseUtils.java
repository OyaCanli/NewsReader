package com.example.oya.newsreader.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
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

    private static final int SYNCH_FLEXTIME_SECONDS = (int) (TimeUnit.HOURS.toSeconds(1));
    private static final String BACKUP_JOB_TAG = "news_backup_tag";
    private static boolean sInitialized;

    synchronized public static void scheduleNewsBackUp(@NonNull final Context context){
        if(sInitialized) return;
        //Check users preferred back up frequency from sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredBackUpFrequency = sharedPreferences.getString(context.getString(R.string.pref_key_backUpFrequency), context.getString(R.string.pref_backUpFrequency_default));
        final int BACKUP_INTERVAL_HOURS = Integer.valueOf(preferredBackUpFrequency);
        if(BACKUP_INTERVAL_HOURS == 0) return;
        else {
            final int BACKUP_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(BACKUP_INTERVAL_HOURS));
            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
            Job.Builder newsCheckerJobBuilder = dispatcher.newJobBuilder()
                    .setService(NewsBackUpService.class)
                    .setTag(BACKUP_JOB_TAG)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(BACKUP_INTERVAL_SECONDS, BACKUP_INTERVAL_SECONDS + SYNCH_FLEXTIME_SECONDS))
                    .setReplaceCurrent(true);

            //Check users preferred conditions for back up
            boolean onlyOnWifi = sharedPreferences.getBoolean(context.getString(R.string.only_on_wifi_key), context.getResources().getBoolean(R.bool.pref_only_on_wifi_default));
            boolean onlyWhenDeviceIdle = sharedPreferences.getBoolean(context.getString(R.string.pref_key_only_when_device_idle), context.getResources().getBoolean(R.bool.pref_only_when_device_idle_default));
            boolean onlyWhenCharging = sharedPreferences.getBoolean(context.getString(R.string.pref_key_only_on_charge), context.getResources().getBoolean(R.bool.pref_only_on_charge_default));

            if(onlyOnWifi) newsCheckerJobBuilder.setConstraints(Constraint.ON_UNMETERED_NETWORK);
            else newsCheckerJobBuilder.setConstraints(Constraint.ON_ANY_NETWORK);

            if(onlyWhenDeviceIdle) newsCheckerJobBuilder.setConstraints(Constraint.DEVICE_IDLE);

            if(onlyWhenCharging) newsCheckerJobBuilder.setConstraints(Constraint.DEVICE_CHARGING);

            dispatcher.schedule(newsCheckerJobBuilder.build());
            sInitialized = true;
        }
    }

    public static void cancelBackingUps(@NonNull final Context context){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(BACKUP_JOB_TAG);
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
