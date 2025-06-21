package com.mshdabiola.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.data.repository.INotePadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notepadRepository: INotePadRepository,
    private val imageToText: ImageToText,
) : ViewModel() {

    private val id = savedStateHandle.toRoute<GalleryArg>().id
    val galleryUiState = notepadRepository
        .getOneNotePad(id)
        .mapLatest { note ->
            GalleryUiState(
                images = note
                    ?.images
                    ?.filter { !it.isDrawing }
                    ?: emptyList(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = GalleryUiState(),
        )

    suspend fun onImage(path: String) {
        try {
            // val image = notePad.images[index]
            val text = try {
                imageToText.toText(path)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
            var note = notepadRepository.getOneNotePad(id).first()!!
            note =
                note.copy(detail = "${note.detail}\n$text")
            notepadRepository.upsert(note)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteImage(id: Long) {
        viewModelScope.launch {
            notepadRepository.deleteImageNote(id)
        }
    }
}
