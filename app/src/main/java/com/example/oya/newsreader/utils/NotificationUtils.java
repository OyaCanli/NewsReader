package com.example.oya.newsreader.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.example.oya.newsreader.R;
import com.example.oya.newsreader.model.NewsArticle;
import com.example.oya.newsreader.ui.DetailsActivity;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class NotificationUtils {

    private static final int NEWS_NOTIFICATION_ID = 4567;
    private static final String NOTIFICATION_CHANNEL_ID = "news_notification_channel";

    private static final int REMINDER_INTERVAL_MINUTES = 30;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNCH_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;

    private static final String REMINDER_JOB_TAG = "news_updater_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleNotifications(@NonNull final Context context){
        if(sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job newsCheckerJob = dispatcher.newJobBuilder()
                .setService(NewsReminderService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS, REMINDER_INTERVAL_SECONDS + SYNCH_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(newsCheckerJob);
        sInitialized = true;
    }

    public static void cancelNotifications(@NonNull final Context context){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(REMINDER_JOB_TAG);
    }

    public static void informUserOfTheNewArticle(Context context, List<NewsArticle> list){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "New Article Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(list.get(0).getTitle())
                .setContentText(Html.fromHtml(list.get(0).getArticleTrail()))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(list.get(0).getArticleTrail())))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, list))
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(NEWS_NOTIFICATION_ID, notificationBuilder.build());

    }

    private static PendingIntent contentIntent(Context context, List<NewsArticle> list){
        Intent startActivity = new Intent(context, DetailsActivity.class);
        startActivity.putExtra(Constants.CHOSEN_ARTICLE, list.get(0));

        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(startActivity)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
