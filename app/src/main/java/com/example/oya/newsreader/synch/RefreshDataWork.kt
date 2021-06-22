package com.example.oya.newsreader.synch

import android.content.Context
import android.database.SQLException
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.canlioya.core.repository.INewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@HiltWorker
class RefreshDataWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: INewsRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.refreshAllData()
            Result.success()
        } catch (e: HttpException) {
            Timber.e(e)
            Result.failure()
        } catch (e: IOException) {
            Timber.e(e)
            Result.failure()
        } catch (e: SQLException) {
            Timber.e(e)
            Result.failure()
        } catch(e : Throwable) {
            Timber.e(e)
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "cleanTrashTask"
    }
}