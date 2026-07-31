package com.example.placeslikee.workmanger

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.api.LogDescriptor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: MarkersSyncManager
): CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        return try{
            syncManager.sync()
            Result.success()
        }
        catch (e: Exception){
            Log.d("my log", "doWork: $e")
            if(runAttemptCount < 3){
                Result.retry()
            }
            else{
                Result.failure()
            }
        }
    }

}