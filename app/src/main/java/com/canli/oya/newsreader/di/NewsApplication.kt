package com.canli.oya.newsreader.di

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.canli.oya.newsreader.data.Interactors
import com.canli.oya.newsreader.notification.NotificationUtils
import com.canli.oya.newsreader.synch.SyncUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var interactors: Interactors

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        // Sync news immediately
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        applicationScope.launch {
            interactors.refreshAllData()
        }

        // Schedule background sync
        syncUtils.scheduleSyncNewsJob()

        // Schedule notifications
        notificationUtils.scheduleNotificationJob()
    }
}
