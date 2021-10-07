package com.canli.oya.newsreader.synch

import android.content.Context
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.canlioya.data.IUserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncUtils @Inject constructor(
    private val userPreferences: IUserPreferences,
    @ApplicationContext private val applicationContext: Context
) {

    fun scheduleSyncNewsJob() {
        if (!userPreferences.isSyncEnabled()) {
            return // If sync is disabled, don't schedule syncs
        }

        val networkType =
            if (userPreferences.shouldSyncOnlyOnWifi()) NetworkType.UNMETERED else NetworkType.CONNECTED

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(userPreferences.shouldSyncOnlyWhenCharging())
            .setRequiredNetworkType(networkType)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(userPreferences.shouldSyncOnlyWhenIdle())
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWork>(
            userPreferences.getBackUpFrequency(),
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            RefreshDataWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    fun cancelBackUps() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(RefreshDataWork.WORK_NAME)
    }
}
