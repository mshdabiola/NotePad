package com.mshdabiola.worker

import android.content.Context
import androidx.startup.AppInitializer
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.mshdabiola.naijaludo.model.LudoGameState
import com.mshdabiola.worker.util.Converter
import com.mshdabiola.worker.work.SaveWorker

object Saver {
    lateinit var workManager: WorkManager
    fun initialize(context: Context) {
        val saver = AppInitializer.getInstance(context)
            .initializeComponent(SaverInitializer::class.java)
        workManager = saver.workManager
    }

    private fun save(workName: String, players: String, pawns: String, id: Long) {
        workManager
            .enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                SaveWorker.startUpSaveWork(players, pawns, id)
            )

    }

    fun saveGame(ludoGameState: LudoGameState, id: Long) {
        val pair = Converter.gameToString(
            ludoGameState.listOfPlayer,
            ludoGameState.listOfPawn,
            id
        )
        save("updater", pawns = pair.first, players = pair.second, id = id)
    }
}
