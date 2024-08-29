/*
 *abiola 2022
 */

package com.mshdabiola.detail

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.detail.navigation.DetailArgs
import com.mshdabiola.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
) : ViewModel() {
    private val topicArgs: DetailArgs = DetailArgs(savedStateHandle)
    val id = topicArgs.id

    private val note = MutableStateFlow<Note?>(Note())

    val title = TextFieldState()
    val content = TextFieldState()

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (id > 0) {
                val initNOte = noteRepository.getOne(id)
                    .first()
                note.update { initNOte }

                if (initNOte != null) {
                    title.edit {
                        this.append(initNOte.title)
                    }
                    content.edit {
                        append(initNOte.content)
                    }
                }
            }
            _state.update { DetailState.Success(id) }

            note
                .collectLatest {
                    onContentChange(it)
                }
        }

        viewModelScope.launch {
            snapshotFlow { title.text }
                .debounce(500)
                .collectLatest { text ->
                    note.update { it?.copy(title = text.toString()) }
                }
        }
        viewModelScope.launch {
            snapshotFlow { content.text }
                .debounce(500)
                .collectLatest { text ->
                    note.update { it?.copy(content = text.toString()) }
                }
        }
    }

    private suspend fun onContentChange(note: Note?) {
        if (note?.title?.isNotBlank() == true || note?.content?.isNotBlank() == true) {
            val id = noteRepository.upsert(note)
            if (note.id == -1L) {
                this@DetailViewModel.note.update { note.copy(id = id) }
            }
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            note.value?.id?.let { noteRepository.delete(it) }
        }
    }
}
