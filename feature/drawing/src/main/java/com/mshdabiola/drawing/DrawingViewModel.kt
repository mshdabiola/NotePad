package com.mshdabiola.drawing

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.model.NoteImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val contentManager: ContentManager,
    private val noteImageRepository: NoteImageRepository
) : ViewModel() {

    val noteId = savedStateHandle.get<Long>(noteIdArg)!!
    val imageID = savedStateHandle.get<Long>(imageIdArg)!!


    var drawingUiState by mutableStateOf(
        DrawingUiState(
            filePath = contentManager.getImagePath(
                imageID
            )
        )
    )







    fun saveImage(bitmap: Bitmap) {
        viewModelScope.launch {


            val path = contentManager.getImagePath(imageID)

            noteImageRepository.upsert(NoteImage(imageID, noteId, path))

            contentManager.saveBitmap(path, bitmap)
        }
    }

    fun deleteImage() {
        viewModelScope.launch(Dispatchers.IO) {
            noteImageRepository.delete(imageID)
            File(contentManager.getImagePath(imageID)).delete()
        }

    }


}