package com.canli.oya.newsreader.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.canli.oya.newsreader.notification.NotificationUtils
import com.canli.oya.newsreader.synch.SyncUtils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class NewsApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncUtils: SyncUtils

    @Inject
    lateinit var notificationUtils: NotificationUtils

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        syncUtils.scheduleSyncNewsJob()

        notificationUtils.scheduleNotificationJob()
    }
}
