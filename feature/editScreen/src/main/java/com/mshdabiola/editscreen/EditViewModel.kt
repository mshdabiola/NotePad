package com.mshdabiola.editscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.editscreen.state.NoteUiState
import com.mshdabiola.editscreen.state.toNote
import com.mshdabiola.editscreen.state.toNotePadUiState
import com.mshdabiola.model.NotePad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notePadRepository: NotePadRepository

) : ViewModel() {

    private val editArg = EditArg(savedStateHandle)
    var notePadUiState by mutableStateOf(NotePad().toNotePadUiState())


    init {
        viewModelScope.launch {
            Log.e("Editviewmodel", "${editArg.id}")
            notePadUiState = if (editArg.id == (-1).toLong()) {
                NotePad().toNotePadUiState()
            } else {
                notePadRepository.getNotePad(editArg.id).toNotePadUiState()
            }
        }

        viewModelScope.launch {
            snapshotFlow {
                notePadUiState
            }
                .map { it.note }
                .distinctUntilChanged { old, new -> old == new }
                .collectLatest {
                    Log.e("flow", "$it")
                    insertNote(it)
                }
        }

    }


    private suspend fun insertNote(noteUiState: NoteUiState) {


        if (noteUiState.title.isNotBlank() || noteUiState.detail.isNotBlank()) {
            if (noteUiState.id == null) {
                val id = notePadRepository.insertNote(noteUiState.toNote())
                savedStateHandle[parameterId] = id
                val note = notePadUiState.note.copy(id = id)
                notePadUiState = notePadUiState.copy(note = note)
                //noteState = noteUiState.copy(id = id)

                } else {
                    notePadRepository.insertNote(noteUiState.toNote())

                }
            }

    }

    fun onTitleChange(title: String) {
        val note = notePadUiState.note.copy(title = title)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun onDetailChange(detail: String) {
        val note = notePadUiState.note.copy(detail = detail)
        notePadUiState = notePadUiState.copy(note = note)
    }

}