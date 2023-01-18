package com.mshdabiola.editscreen

import android.media.MediaRecorder
import javax.inject.Inject

class VoiceRecorder
@Inject constructor(

) {
    private var mediaRecorder: MediaRecorder? = null

    fun start(path: String) {
        try {
            mediaRecorder = MediaRecorder()
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(path)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun stop() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

}