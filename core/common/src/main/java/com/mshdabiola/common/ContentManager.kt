package com.mshdabiola.common

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class ContentManager
@Inject constructor(
    @ApplicationContext val context: Context
) {

    private val photoDir = context.filesDir.absolutePath + "/photo"
    private val voiceDir = context.filesDir.absolutePath + "/voice"

    fun saveImage(uri: Uri, currentTime: Long) {
        createImageDir()
        val outputStream = FileOutputStream(File(photoDir, "Image_$currentTime.jpg"))

        context.contentResolver.openInputStream(uri).use {
            it?.copyTo(outputStream)
            outputStream.close()
        }
    }

    fun saveVoice(uri: Uri, currentTime: Long) {
        createVoiceDir()
        val outputStream = FileOutputStream(File(voiceDir, "Voice_$currentTime.amr"))

        context.contentResolver.openInputStream(uri).use {
            it?.copyTo(outputStream)
            outputStream.close()
        }
    }

    fun pictureUri(id: Long): Uri {
        createImageDir()
        val file = File(photoDir, "Image_$id.jpg")

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        Log.e("uri", "$uri")
        return uri
    }

    fun getImagePath(data: Long): String {
        return "$photoDir/Image_$data.jpg"
    }

    fun getVoicePath(data: Long): String {
        return "$voiceDir/Voice_$data.amr"
    }

    private fun createVoiceDir() {
        val file = File(voiceDir)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    private fun createImageDir() {
        val file = File(photoDir)
        if (!file.exists()) {
            file.mkdir()
        }
    }

    fun saveBitmap(path: String, bitmap: Bitmap) {
        createImageDir()
        File(path).outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

    }


}