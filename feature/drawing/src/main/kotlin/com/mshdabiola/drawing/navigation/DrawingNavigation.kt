package com.mshdabiola.drawing.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.drawing.DrawingScreen
import com.mshdabiola.drawing.DrawingViewModel
import kotlinx.serialization.Serializable

fun EntryProviderBuilder<NavKey>.drawingScreen(
    onBack: () -> Unit,
) {
    entry<DrawingArgs> { key ->
        val drawingViewModel = hiltViewModel<DrawingViewModel, DrawingViewModel.Factory>(
            creationCallback = { factory -> factory.create(key) },
        )
        val state = drawingViewModel.drawingState.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val onSend = {
//            val file = File(state.value.filePath!!)
//            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
//            val intent = ShareCompat.IntentBuilder(context)
//                .setType("image/*")
//                .setStream(uri)
//                .setChooserTitle("NotePad")
//                .createChooserIntent()
//
//            context.startActivity(intent)
        }
        val onCopy = {
//            val file = File(state.value.filePath!!)
//            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
//
//            val content = context.contentResolver
//            val clip = ClipData.newUri(content, "image", uri)
//            val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            c.setPrimaryClip(clip)
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

fun NavBackStack.navigateToDrawing(drawingArgs: DrawingArgs) {
    add(drawingArgs)
}

@Serializable
data class DrawingArgs(
    val noteId: Long,
    val id: Long?,
) : NavKey
