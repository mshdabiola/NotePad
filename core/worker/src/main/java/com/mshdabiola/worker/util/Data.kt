package com.mshdabiola.worker.util

import androidx.work.CoroutineWorker
import androidx.work.Data
import kotlin.reflect.KClass

internal const val WORKER_CLASS_NAME = "RouterWorkerDelegateClassName"
internal const val PATHS = "paths"
internal const val IMAGE_Id = "image_id"
internal const val NOTE_ID="note_id"
internal fun KClass<out CoroutineWorker>.delegatedData(paths: String, imageId:Long,noteId:Long) =
    Data.Builder()
        .putString(WORKER_CLASS_NAME, qualifiedName)
        .putString(PATHS, paths)
        .putLong(IMAGE_Id, imageId)
        .putLong(NOTE_ID,noteId)
        .build()