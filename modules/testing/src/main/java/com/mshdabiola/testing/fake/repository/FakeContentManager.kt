package com.mshdabiola.testing.fake.repository

import android.graphics.Bitmap
import com.mshdabiola.common.IContentManager
import java.io.File
import javax.inject.Inject

class FakeContentManager @Inject constructor() : IContentManager {
    override fun saveImage(uri: String): Long {
        return 1
    }

    override fun saveVoice(uri: String): Long {
        return 1
    }

    override fun pictureUri(): String {
        return ""
    }

    override fun getImagePath(data: Long): String {
        return ""
    }

    override fun getVoicePath(data: Long): String {
        return ""
    }

    override fun saveBitmap(path: String, bitmap: Bitmap) {
    }

    override fun dataFile(drawingId: Long): File {
        return File("")
    }
}
