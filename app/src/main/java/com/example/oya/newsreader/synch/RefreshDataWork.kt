package com.example.oya.newsreader.synch

import android.content.Context
import android.database.SQLException
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.canlioya.core.repository.INewsRepository
import com.canlioya.data.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshDataWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: INewsRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.refreshData()
            Result.success()
        } catch (e: SQLException) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "cleanTrashTask"
    }
}