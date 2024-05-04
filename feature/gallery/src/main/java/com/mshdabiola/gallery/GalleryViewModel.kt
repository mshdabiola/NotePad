package com.mshdabiola.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.ui.state.toNoteImageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteImageRepository: NoteImageRepository,
    private val contentManager: ContentManager
) : ViewModel() {

    private val id = savedStateHandle.get<Long>(noteIdStr)!!
    private val currentIndex = savedStateHandle.get<Long>(currentId)!!

    private val _galleryUiState = MutableStateFlow(GalleryUiState())
    val galleryUiState = _galleryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteImageRepository.getImageByNoteId(id)
                .map { noteImages -> noteImages.filter { !it.isDrawing } }
                .collectLatest { noteImages ->
                    val reverseImages = noteImages.reversed()
                    val index = reverseImages.indexOfFirst { it.id == currentIndex }
                    _galleryUiState.value = galleryUiState
                        .value
                        .copy(
                            images =
                            reverseImages.map {
                                it.toNoteImageUiState(contentManager::getImagePath)
                            }.toImmutableList(),
                            currentIndex = index,
                        )
                }
        }
    }

    fun deleteImage(id: Long) {
        viewModelScope.launch {
            noteImageRepository.delete(id)
        }
    }
}
