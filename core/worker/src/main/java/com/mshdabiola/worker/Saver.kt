package com.mshdabiola.worker

import android.content.Context
import androidx.startup.AppInitializer
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mshdabiola.model.DrawPath
import com.mshdabiola.worker.util.Converter
import com.mshdabiola.worker.work.SaveWorker

object Saver {
    lateinit var workManager: WorkManager
    fun initialize(context: Context) {
        val saver = AppInitializer.getInstance(context)
            .initializeComponent(SaverInitializer::class.java)
        workManager = saver.workManager
    }

    private fun save(workName: String,imageId:Long,noteId: Long) {
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                SaveWorker.startUpSaveWork( imageId,noteId)
            )

    }

    fun saveGame( imageId:Long,noteId:Long) {

        save("saver",imageId,noteId)
    }
}
