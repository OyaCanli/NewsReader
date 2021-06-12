package com.example.oya.newsreader.synch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.oya.newsreader.R;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public final class ScheduleSyncUtils {

    private static final int SYNCH_FLEXTIME_SECONDS = (int) (TimeUnit.HOURS.toSeconds(1));
    private static final String BACKUP_JOB_TAG = "news_backup_tag";
    private static boolean sInitialized;

    public static void scheduleNewsBackUp(@NonNull final Context context) {
        //Check users preferred back up frequency from sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredBackUpFrequency = sharedPreferences.getString(context.getString(R.string.pref_key_backUpFrequency), context.getString(R.string.pref_backUpFrequency_default));
        final int BACKUP_INTERVAL_HOURS = Integer.valueOf(preferredBackUpFrequency);
        if (BACKUP_INTERVAL_HOURS == 0) return;
        else {
            final int BACKUP_INTERVAL_SECONDS = (int) (TimeUnit.HOURS.toSeconds(BACKUP_INTERVAL_HOURS));
            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
            Job.Builder newsCheckerJobBuilder = dispatcher.newJobBuilder()
                    .setService(NewsFirebaseJobService.class)
                    .setTag(BACKUP_JOB_TAG)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(BACKUP_INTERVAL_SECONDS, BACKUP_INTERVAL_SECONDS + SYNCH_FLEXTIME_SECONDS))
                    .setReplaceCurrent(true);

            //Check users preferred conditions for back up
            boolean onlyOnWifi = sharedPreferences.getBoolean(context.getString(R.string.only_on_wifi_key), context.getResources().getBoolean(R.bool.pref_only_on_wifi_default));
            boolean onlyWhenDeviceIdle = sharedPreferences.getBoolean(context.getString(R.string.pref_key_only_when_device_idle), context.getResources().getBoolean(R.bool.pref_only_when_device_idle_default));
            boolean onlyWhenCharging = sharedPreferences.getBoolean(context.getString(R.string.pref_key_only_on_charge), context.getResources().getBoolean(R.bool.pref_only_on_charge_default));

            if (onlyOnWifi) newsCheckerJobBuilder.setConstraints(Constraint.ON_UNMETERED_NETWORK);
            else newsCheckerJobBuilder.setConstraints(Constraint.ON_ANY_NETWORK);

            if (onlyWhenDeviceIdle) newsCheckerJobBuilder.setConstraints(Constraint.DEVICE_IDLE);

            if (onlyWhenCharging) newsCheckerJobBuilder.setConstraints(Constraint.DEVICE_CHARGING);

            dispatcher.schedule(newsCheckerJobBuilder.build());

        }
    }

    public static void cancelBackingUps(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(BACKUP_JOB_TAG);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;

        sInitialized = true;

        scheduleNewsBackUp(context);

    }

}
