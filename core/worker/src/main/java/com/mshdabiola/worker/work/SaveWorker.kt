package com.mshdabiola.worker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.mshdabiola.database.dao.LudoDao
import com.mshdabiola.database.dao.PawnDao
import com.mshdabiola.database.dao.PlayerDao
import com.mshdabiola.database.model.LudoEntity
import com.mshdabiola.worker.util.ID
import com.mshdabiola.worker.util.PAWNS
import com.mshdabiola.worker.util.PLAYERS
import com.mshdabiola.worker.util.PawnPojo
import com.mshdabiola.worker.util.PlayerPojo
import com.mshdabiola.worker.util.delegatedData
import com.mshdabiola.worker.util.saverConstraints
import com.mshdabiola.worker.util.syncForegroundInfo
import com.mshdabiola.worker.util.toPawnEntity
import com.mshdabiola.worker.util.toPlayerEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber

@HiltWorker
class SaveWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val ludoDao: LudoDao,
    private val pawnDao: PawnDao,
    private val playerDao: PlayerDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val players = workerParams.inputData.getString(PLAYERS)
        val pawns = workerParams.inputData.getString(PAWNS)
        val id = workerParams.inputData.getLong(ID, 6)

        val playerss = players?.let { Json.decodeFromString<List<PlayerPojo>>(it) }
        val pawnss = pawns?.let { Json.decodeFromString<List<PawnPojo>>(pawns) }
        Timber.e(playerss?.toString())
        Timber.e(pawnss?.toString())
        Timber.e("$id")

        ludoDao.upsert(LudoEntity(id))

        if (pawnss != null) {
            pawnDao.upsertMany(pawnss.map { it.toPawnEntity() })
        }
        if (playerss != null) {
            playerDao.upsertMany(playerss.map { it.toPlayerEntity() })
        }

        Result.success()
    }


    companion object {
        fun startUpSaveWork(players: String, pawns: String, id: Long) =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(saverConstraints)
                .setInputData(SaveWorker::class.delegatedData(players, pawns, id))
                .build()
    }
}

