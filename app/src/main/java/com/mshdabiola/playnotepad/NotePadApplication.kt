package com.mshdabiola.playnotepad

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NotePadApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        //Saver.initialize(applicationContext)

        if (packageName.contains("debug")) {
            Timber.plant(Timber.DebugTree())
            Timber.e("log on app create")
        }
    }

}
