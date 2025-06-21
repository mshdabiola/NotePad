package com.mshdabiola.drawing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import java.io.File

fun NavGraphBuilder.drawingScreen(
    onBack: () -> Unit,
) {
    composable<DrawingArgs> {
        val drawingViewModel = hiltViewModel<DrawingViewModel>()
        val state = drawingViewModel.drawingState.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val onSend = {
            val file = File(state.value.filePath!!)
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val intent = ShareCompat.IntentBuilder(context)
                .setType("image/*")
                .setStream(uri)
                .setChooserTitle("NotePad")
                .createChooserIntent()

            context.startActivity(intent)
        }
        val onCopy = {
            val file = File(state.value.filePath!!)
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

            val content = context.contentResolver
            val clip = ClipData.newUri(content, "image", uri)
            val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            c.setPrimaryClip(clip)
        }

        DrawingScreen(
            controller = drawingViewModel.controller,
            drawingUiState = state.value,
            onBackk = onBack,
            onCopy = onCopy,
            onSend = onSend,
            onDeleteImage = {},
        )
    }
}

fun NavController.navigateToDrawing(drawingArgs: DrawingArgs) {
    navigate(drawingArgs)
}

@Serializable
data class DrawingArgs(
    val noteId: Long,
    val imageId: Long,
    val width: Int = 0,
    val height: Int = 0,
    val density: Float = 0f,
)
