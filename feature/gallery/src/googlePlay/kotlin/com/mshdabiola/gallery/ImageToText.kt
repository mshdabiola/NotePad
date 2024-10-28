package com.mshdabiola.gallery

import android.content.Context
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageToText @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun toText(string: String) = suspendCoroutine { cont ->
        val uri =
            FileProvider.getUriForFile(context, context.packageName + ".provider", File(string))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val image = InputImage.fromFilePath(context, uri)

        recognizer.process(image)
            .addOnSuccessListener {
                cont.resume(it?.text ?: "")
            }
            .addOnFailureListener {
                it.printStackTrace()
                cont.resume("")
            }
    }
}
