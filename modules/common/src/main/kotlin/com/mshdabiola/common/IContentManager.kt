package com.mshdabiola.common

import android.graphics.Bitmap
import java.io.File

interface IContentManager {
    fun saveImage(uri: String): Long
    fun saveVoice(uri: String): Long
    fun pictureUri(): String
    fun getImagePath(data: Long): String
    fun getVoicePath(data: Long): String
    fun saveBitmap(path: String, bitmap: Bitmap)
    fun dataFile(drawingId: Long): File
}
