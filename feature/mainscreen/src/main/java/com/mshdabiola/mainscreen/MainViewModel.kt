package com.mshdabiola.mainscreen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.mainscreen.state.MainState
import com.mshdabiola.mainscreen.state.toNotePadUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notepadRepository: NotePadRepository,
    private val contentManager: ContentManager
) : ViewModel() {


    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    init {

        viewModelScope.launch {
            notepadRepository.getNotePads().map { notes ->
                notes.map { it.toNotePadUiState() }
            }
                .collect {
                    _mainState.value = mainState.value.copy(notePads = it.toImmutableList())
                }
        }
    }

    fun savePhoto(uri: Uri, id: Long) {
        viewModelScope.launch {
            contentManager.saveImage(uri, id)
        }
    }

    fun saveVoice(uri: Uri, id: Long) {
        viewModelScope.launch {
            contentManager.saveVoice(uri, id)
        }
    }

    fun getPhotoUri(id: Long): Uri {
        return contentManager.pictureUri(id)
    }

}