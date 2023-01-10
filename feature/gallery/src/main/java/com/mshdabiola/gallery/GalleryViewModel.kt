package com.mshdabiola.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.database.repository.NoteImageRepository
import com.mshdabiola.designsystem.component.state.toNoteImageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val noteImageRepository: NoteImageRepository
) : ViewModel() {

    private val id = savedStateHandle.get<Long>(noteIdStr)!!
    private val currentIndex = savedStateHandle.get<Long>(currentId)!!

    private val _galleryUiState = MutableStateFlow(GalleryUiState())
    val galleryUiState = _galleryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteImageRepository.getImageByNoteId(id)
                .collectLatest { noteImages ->
                    val index = noteImages.indexOfFirst { it.id == currentIndex }
                    _galleryUiState.value = galleryUiState
                        .value
                        .copy(
                            images =
                            noteImages.map {
                                it.toNoteImageUiState()
                            }.toImmutableList(),
                            currentIndex = index
                        )
                }
        }
    }

}