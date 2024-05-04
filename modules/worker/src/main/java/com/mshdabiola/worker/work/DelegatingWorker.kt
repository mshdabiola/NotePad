package com.mshdabiola.worker.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.mshdabiola.worker.di.HiltWorkerFactoryEntryPoint
import com.mshdabiola.worker.util.WORKER_CLASS_NAME
import dagger.hilt.android.EntryPointAccessors
import timber.log.Timber

class DelegatingWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    private val workerClassName =
        workerParams.inputData.getString(WORKER_CLASS_NAME) ?: ""

    init {
        Timber.e("worker name $workerParams")
    }

    private val delegateWorker =
        EntryPointAccessors.fromApplication<HiltWorkerFactoryEntryPoint>(appContext)
            .hiltWorkerFactory()
            .createWorker(appContext, workerClassName, workerParams)
                as? CoroutineWorker
            ?: throw IllegalArgumentException("Unable to find appropriate worker")


    override suspend fun getForegroundInfo(): ForegroundInfo =
        delegateWorker.getForegroundInfo()

    override suspend fun doWork(): Result =
        delegateWorker.doWork()

}
