package com.mshdabiola.common

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface IContentManager {
    fun saveImage(uri: Uri, currentTime: Long)
    fun saveVoice(uri: Uri, currentTime: Long)
    fun pictureUri(id: Long): Uri
    fun getImagePath(data: Long): String
    fun getVoicePath(data: Long): String
    fun saveBitmap(path: String, bitmap: Bitmap)
    fun dataFile(drawingId: Long): File
}
