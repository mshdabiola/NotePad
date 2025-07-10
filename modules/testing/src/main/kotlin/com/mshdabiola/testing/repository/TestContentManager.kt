package com.mshdabiola.testing.repository

import android.graphics.Bitmap
import com.mshdabiola.common.IContentManager
import java.io.File

class TestContentManager : IContentManager {

    var imageToTextResult: String = "Extracted text from content manager"
    var imageToTextShouldThrowError: Boolean = false
    var lastPathForImageToText: String? = null

    override fun saveImage(uri: String): Long {
        return 1
    }

    override fun saveVoice(uri: String): Long {
        return 1
    }

    override fun pictureUri(): String {
        return ""
    }

    override fun getImagePath(id: Long): String {
        return "/fake/content/path/image_$id.jpg"
    }

    override fun getVoicePath(data: Long): String {
        return ""
    }

    override fun saveBitmap(path: String, bitmap: Bitmap) {
    }

    override fun dataFile(drawingId: Long): File {
        return File("")
    }

    override fun getAudioLength(path: String): Long {
        return 2
    }

    // Add this method based on your IContentManager interface
    override fun imageToText(path: String): String {
        lastPathForImageToText = path
        if (imageToTextShouldThrowError) {
            throw Exception("ContentManager imageToText error")
        }
        return imageToTextResult
    }
}
