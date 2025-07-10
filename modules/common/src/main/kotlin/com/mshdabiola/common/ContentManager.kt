package com.mshdabiola.common

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class ContentManager
@Inject constructor(
    @ApplicationContext val context: Context,
    val imageToText: ImageToText,
) : IContentManager {
    lateinit var retriever: MediaMetadataRetriever
    private val photoDir = context.filesDir.absolutePath + "/photo"
    private val voiceDir = context.filesDir.absolutePath + "/voice"

    override fun saveImage(uri: String): Long {
        return try {
            val currentTime = System.currentTimeMillis()
            createImageDir()
            val outputStream = FileOutputStream(File(photoDir, "Image_$currentTime.jpg"))

            context.contentResolver.openInputStream(Uri.parse(uri)).use {
                it?.copyTo(outputStream)
                outputStream.close()
            }
            currentTime
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun saveVoice(uri: String): Long {
        return try {
            val currentTime = System.currentTimeMillis()
            createVoiceDir()
            val outputStream = FileOutputStream(File(voiceDir, "Voice_$currentTime.amr"))

            context.contentResolver.openInputStream(Uri.parse(uri)).use {
                it?.copyTo(outputStream)
                outputStream.close()
            }
            currentTime
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun pictureUri(): String {
        createImageDir()
        val file = File(photoDir, "Image_$2.jpg")

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        return uri.toString()
    }

    override fun getImagePath(data: Long): String {
        return "$photoDir/Image_$data.jpg"
    }

    override fun getVoicePath(data: Long): String {
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

    override fun saveBitmap(path: String, bitmap: Bitmap) {
        createImageDir()
        File(path).outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    override fun dataFile(drawingId: Long): File {
        val dir = File(context.filesDir.absolutePath + "/drawingfile")
        if (dir.exists().not()) {
            dir.mkdir()
        }

        return File(dir, "data_$drawingId.json")
    }

    override fun getAudioLength(path: String): Long {
        if (!::retriever.isInitialized) {
            retriever = MediaMetadataRetriever()
        }

        try {
            retriever.setDataSource(path)
            return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0
        } catch (e: IllegalArgumentException) {
            // Handle the exception, log error, etc.
            e.printStackTrace()
            return 0
        } finally {
            retriever.release()
        }
    }

    override fun imageToText(path: String): String {
        return imageToText.toText(path)
    }
}
