package com.mshdabiola.drawing

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.model.NoteImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val contentManager: ContentManager,
    private val noteImageRepository: NoteImageRepository
) : ViewModel() {

    val noteId = savedStateHandle.get<Long>(noteIdArg)!!

    var path: String? = null //
    var imageID = 0L

    var drawingUiState by mutableStateOf(DrawingUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            imageID =
                (noteImageRepository.getImageByNoteId(noteId).firstOrNull()?.lastOrNull()?.id?.plus(
                    1
                )) ?: 0
        }
    }


    fun onPointChange(offset: Offset, mode: MODE) {
        Log.e("canvas ", "PathData(x = ${offset.x}f, ${offset.y}f,mode=MODE.${mode}),")
        drawingUiState = if (mode == MODE.UP) {
            val paths = drawingUiState.paths.toMutableList()
            val last = paths.last()
            paths.add(last.copy().copy(mode = mode))
            drawingUiState.copy(paths = paths.toImmutableList())
        } else {
            val paths = drawingUiState.paths.toMutableList()
            paths.add(PathData(x = offset.x, y = offset.y, mode = mode))
            drawingUiState.copy(paths = paths.toImmutableList())
        }
    }


    fun saveImage(bitmap: Bitmap) {
        viewModelScope.launch {

            if (path == null) {
                path = contentManager.getImagePath(System.currentTimeMillis())

                noteImageRepository.upsert(NoteImage(imageID, noteId, path!!))

                contentManager.saveBitmap(path!!, bitmap)

            } else {
                contentManager.saveBitmap(path!!, bitmap)
            }


        }
    }


}