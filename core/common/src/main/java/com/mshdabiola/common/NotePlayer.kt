package com.mshdabiola.common

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotePlayer
@Inject constructor(
    @ApplicationContext val context: Context
) {
    val mediaPlayer = MediaPlayer()

    fun playMusic(path: String) {

        mediaPlayer.reset()
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()

        mediaPlayer.start()


    }

    fun onClose() {
        mediaPlayer.release()
    }


}