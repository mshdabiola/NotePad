package com.mshdabiola.editscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.editscreen.state.NoteUiState
import com.mshdabiola.editscreen.state.toNote
import com.mshdabiola.editscreen.state.toNoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notePadRepository: NotePadRepository

) : ViewModel() {

    private val editArg = EditArg(savedStateHandle)
    var noteState by mutableStateOf(NoteUiState())


    init {
        viewModelScope.launch {
            Log.e("Editviewmodel", "${editArg.id}")
            noteState = if (editArg.id == (-1).toLong()) {
                NoteUiState()
            } else {
                notePadRepository.getOneNote(editArg.id).toNoteUiState()
            }
        }

    }


    fun insertNote(noteUiState: NoteUiState) {
        viewModelScope.launch {

            if (noteUiState.title.isNotBlank() || noteUiState.detail.isNotBlank()) {
                if (noteUiState.id == null) {
                    val id = notePadRepository.insertNote(noteUiState.toNote())
                    savedStateHandle[parameterId] = id
                    noteState = noteUiState.copy(id = id)

                } else {
                    notePadRepository.insertNote(noteUiState.toNote())

                }
            }
        }
    }

    fun onTitleChange(title: String) {
        noteState = noteState.copy(title = title)
    }

    fun onDetailChange(detail: String) {
        noteState = noteState.copy(detail = detail)
    }

}