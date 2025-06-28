package com.mshdabiola.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.model.NoteImage
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
    private val noteImageRepository: NoteImageRepository,
    private val imageToText: ImageToText,
    private val getNoteUseCase: GetNoteUseCase,
    private val addAllNoteUseCase: AddAllNoteUseCase,
    private val contentManager: IContentManager,
) : ViewModel() {

    private val galleryArg = savedStateHandle.toRoute<GalleryArg>()
    val galleryUiState = noteImageRepository
        .getByNoteId(galleryArg.id)
        .mapLatest { images ->
            GalleryUiState(
                initIndex = galleryArg.index,
                images = images.map {
                    it.copy(
                        path = contentManager.getImagePath(it.id),
                    )
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = GalleryUiState(
                initIndex = galleryArg.index,
                images = List(galleryArg.total) {
                    NoteImage(
                        id = it.toLong(),
                        path = galleryArg.currentPath,
                    )
                },

            ),
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
            var note = getNoteUseCase(galleryArg.id).first()!!
            note =
                note.copy(note = note.note.copy(detail = "${note.note.detail}\n$text"))
            addAllNoteUseCase(note)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteImage(id: Long) {
        viewModelScope.launch {
            noteImageRepository.delete(id)
        }
    }
}
