package com.mshdabiola.mainscreen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteType
import com.mshdabiola.designsystem.component.state.toLabelUiState
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import com.mshdabiola.designsystem.component.state.toNoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notepadRepository: NotePadRepository,
    private val contentManager: ContentManager,
    private val labelRepository: LabelRepository
) : ViewModel() {


    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    init {


        viewModelScope.launch {
            combine(
                labelRepository.getAllLabels(),
                mainState.map { it.noteType },
                transform = { T1, T2 ->

                    Pair(T1, T2)
                })
                .distinctUntilChanged { old, new -> old == new }
                .collectLatest { pair ->

                    _mainState.value = mainState.value.copy(
                        labels = pair.first.map { it.toLabelUiState() }.toImmutableList()
                    )

                    when (pair.second) {
                        is NoteType.LABEL -> {
                            _mainState.value =
                                mainState.value.copy(notePads = emptyList<NotePadUiState>().toImmutableList())
                        }

                        is NoteType.REMAINDER -> {
                            _mainState.value =
                                mainState.value.copy(notePads = emptyList<NotePadUiState>().toImmutableList())
                        }

                        else -> {
                            notepadRepository.getNotePads(pair.second.toNoteType()).map { notes ->
                                notes.map { it.toNotePadUiState(pair.first) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList()
                                    )
                                }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }
                    }
                }


        }
    }

    fun onSelectCard(id: Long) {
        var listNOtePad = mainState.value.notePads.toMutableList()
        val index = listNOtePad.indexOfFirst { it.note.id == id }
        val notepad = listNOtePad[index]
        val newNotepad = notepad.copy(note = notepad.note.copy(selected = !notepad.note.selected))

        listNOtePad[index] = newNotepad

        _mainState.value = mainState.value.copy(notePads = listNOtePad.toImmutableList())


    }

    fun clearSelected() {
        val listNOtePad =
            mainState.value.notePads.map { it.copy(note = it.note.copy(selected = false)) }
        _mainState.value = mainState.value.copy(notePads = listNOtePad.toImmutableList())
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

    fun setNoteType(noteType: NoteType) {
        _mainState.value = mainState.value.copy(noteType = noteType)
    }


}