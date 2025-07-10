package com.mshdabiola.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.gallery.navigation.GalleryArg
import com.mshdabiola.model.NoteImage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GalleryViewModel.Factory::class)
class GalleryViewModel @AssistedInject constructor(
    @Assisted val galleryArg: GalleryArg,
    private val noteImageRepository: NoteImageRepository,
    private val noteRepository: NoteRepository,
    private val contentManager: IContentManager,
) : ViewModel() {

    val galleryUiState = noteImageRepository
        .getByNoteId(galleryArg.id)
        .mapLatest { images ->
            println("images: $images")
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
                contentManager.imageToText(path)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
            var note = noteRepository.get(galleryArg.id).first()!!
            note =
                note.copy(note = note.note.copy(detail = "${note.note.detail}\n$text"))
            noteRepository.upsert(note)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteImage(id: Long) {
        viewModelScope.launch {
            noteImageRepository.delete(id)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(galleryArg: GalleryArg): GalleryViewModel
    }
}
