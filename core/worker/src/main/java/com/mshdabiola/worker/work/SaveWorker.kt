package com.mshdabiola.worker.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.DrawingPathRepository
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.model.DrawingUtil
import com.mshdabiola.model.NoteImage
import com.mshdabiola.worker.util.DrawPathPojo
import com.mshdabiola.worker.util.IMAGE_Id
import com.mshdabiola.worker.util.NOTE_ID
import com.mshdabiola.worker.util.PATHS
import com.mshdabiola.worker.util.changeToPathAndData
import com.mshdabiola.worker.util.delegatedData
import com.mshdabiola.worker.util.getBitMap
import com.mshdabiola.worker.util.saverConstraints
import com.mshdabiola.worker.util.syncForegroundInfo
import com.mshdabiola.worker.util.toDrawPath
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

@HiltWorker
class SaveWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val contentManager: ContentManager,
    private val noteImageRepository: NoteImageRepository,
    private val drawingPathRepository: DrawingPathRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val paths = workerParams.inputData.getString(PATHS)
        val imageId = workerParams.inputData.getLong(IMAGE_Id, 7)
        val noteId = workerParams.inputData.getLong(NOTE_ID, 7)

        val pathList = paths?.let { Json.decodeFromString<List<DrawPathPojo>>(it) }
        if (pathList != null) {

            val drawPathList = pathList.map { it.toDrawPath() }
            val pathsMap = drawPathList.let { DrawingUtil.toPathMap(it) }

            val re = appContext.resources.displayMetrics
            val bitmap = getBitMap(
                changeToPathAndData(pathsMap),
                re.widthPixels,
                re.heightPixels,
                re.density
            )
            val path = contentManager.getImagePath(imageId)
            contentManager.saveBitmap(path, bitmap)

            if (pathsMap.isEmpty()) {
            drawingPathRepository.delete(imageId)
            noteImageRepository.delete(imageId)
            File(contentManager.getImagePath(imageId)).deleteOnExit()
        } else {
            noteImageRepository.upsert(NoteImage(imageId, noteId,path, true))
            drawingPathRepository.delete(imageId)
            drawingPathRepository.insert(drawPathList)
        }

        }
        Result.success()
    }


    companion object {
        fun startUpSaveWork(paths: String, imageId: Long,noteId:Long) =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(saverConstraints)
                .setInputData(SaveWorker::class.delegatedData(paths, imageId,noteId))
                .build()
    }
}

