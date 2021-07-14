package com.canli.oya.newsreader.notification

import android.app.*
import android.content.Context
import androidx.work.*
import com.canli.oya.newsreader.synch.RefreshDataWork
import com.canlioya.data.IUserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.canli.oya.newsreader.ui.details.DetailsActivity
import android.content.Intent
import com.canli.oya.newsreader.R
import com.canlioya.core.model.NewsArticle
import androidx.core.content.ContextCompat
import android.os.Build
import androidx.core.app.NotificationCompat
import com.canli.oya.newsreader.common.CHOSEN_ARTICLE
import com.canli.oya.newsreader.common.fromHtml


@Singleton
class NotificationUtils @Inject constructor(
    private val userPreferences: IUserPreferences,
    @ApplicationContext private val context: Context
) {

    val NEWS_NOTIFICATION_ID = 4567
    val NOTIFICATION_CHANNEL_ID = "news_notification_channel"

    fun scheduleNotificationJob() {
        if (!userPreferences.isNotificationEnabled()) {
            return //If notifications are disabled, don't schedule the job
        }

        val networkType =
            if (userPreferences.shouldSyncOnlyOnWifi()) NetworkType.UNMETERED else NetworkType.CONNECTED

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(networkType)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWork>(
            30,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RefreshDataWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(NotificationWork.WORK_NAME)
    }

    fun showNotification(article: NewsArticle) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "New Article Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(article.title)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context, article))
                .setAutoCancel(true)
        article.articleTrail?.let {
            notificationBuilder.setContentText(fromHtml(it))
            notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(fromHtml(it)))
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
        }
        notificationManager.notify(NEWS_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun contentIntent(context: Context, article: NewsArticle): PendingIntent? {
        val startActivity = Intent(context, DetailsActivity::class.java)
        startActivity.putExtra(CHOSEN_ARTICLE, article)
        return TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(startActivity)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}