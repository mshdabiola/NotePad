package com.mshdabiola.worker

import android.content.Context
import androidx.startup.Initializer
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer

class SaverInitializer : Initializer<Saver> {
    override fun create(context: Context): Saver {
        val saver = Saver
        saver.workManager = WorkManager.getInstance(context)

        return saver
    }

    override fun dependencies() =
        listOf(WorkManagerInitializer::class.java)

}