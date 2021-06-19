package com.example.oya.newsreader.di

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.canlioya.data.IUserPreferences
import com.example.oya.newsreader.R
import com.example.oya.newsreader.synch.RefreshDataWork
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class NewsApplication : Application(), Configuration.Provider  {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var userPreferences : IUserPreferences

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        GlobalScope.launch {
            setUpSyncNewsJob()
        }
    }

    private fun setUpSyncNewsJob() {
        val networkType = if(userPreferences.shouldSyncOnlyOnWifi()) NetworkType.UNMETERED else NetworkType.CONNECTED

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(userPreferences.shouldSyncOnlyWhenCharging())
            .setRequiredNetworkType(networkType)

            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(userPreferences.shouldSyncOnlyWhenIdle())
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWork>(userPreferences.getBackUpFrequency(), TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}