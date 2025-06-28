package com.mshdabiola.domain

import android.media.MediaMetadataRetriever
import javax.inject.Inject

class AudioLengthUseCase @Inject constructor() {
    operator fun invoke(filePath: String): Long {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
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
}
