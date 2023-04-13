package com.mshdabiola.worker.util

import androidx.work.CoroutineWorker
import androidx.work.Data
import kotlin.reflect.KClass

internal const val WORKER_CLASS_NAME = "RouterWorkerDelegateClassName"
internal const val PLAYERS = "players"
internal const val PAWNS = "pawns"
internal const val ID = "id"
internal fun KClass<out CoroutineWorker>.delegatedData(players: String, pawns: String, id: Long) =
    Data.Builder()
        .putString(WORKER_CLASS_NAME, qualifiedName)
        .putString(PLAYERS, players)
        .putString(PAWNS, pawns)
        .putLong(ID, id)
        .build()