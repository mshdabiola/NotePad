package com.mshdabiola.playnotepad

import android.app.Application
import com.mshdabiola.worker.Saver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotePadApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        Saver.initialize(applicationContext)
    }
}
