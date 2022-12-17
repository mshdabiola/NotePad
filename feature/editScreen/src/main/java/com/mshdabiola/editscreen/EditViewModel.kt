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
import com.mshdabiola.editscreen.state.NotePadUiState
import com.mshdabiola.editscreen.state.toNoteCheckUiState
import com.mshdabiola.editscreen.state.toNotePad
import com.mshdabiola.editscreen.state.toNotePadUiState
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
                notePadRepository.getOneNotePad(editArg.id).toNotePadUiState()
            }
        }

        viewModelScope.launch {
            snapshotFlow {
                notePadUiState
            }

                .distinctUntilChanged { old, new -> old == new }
                .collectLatest {
                    Log.e("flow", "$it")
                    insertNotePad(it)
                }
        }

    }


    private suspend fun insertNotePad(notePad: NotePadUiState) {


        if (notePad.note.title.isNotBlank()
            || notePad.note.detail.isNotBlank()
            || notePad.checks.isNotEmpty()
            || notePad.voices.isNotEmpty()
            || notePad.images.isNotEmpty()
        ) {
            if (notePad.note.id == null) {
                val id = notePadRepository.insertNotepad(notePad.toNotePad())
                savedStateHandle[parameterId] = id
                val note = notePadUiState.note.copy(id = id)
                val notechecks = notePad.checks.toMutableList()
                if (notechecks.isNotEmpty()) {

                    notechecks[0] = notechecks[0].copy(noteId = id)
                }

                notePadUiState = notePadUiState.copy(
                    note = note,
                    checks = notechecks.toImmutableList()
                )

                //noteState = noteUiState.copy(id = id)

            } else {
                notePadRepository.insertNotepad(notePad.toNotePad())

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

    fun addCheck() {
        val size = notePadUiState.checks.size
        val noteId = notePadUiState.note.id
        val noteCheck = NoteCheck(id = size.toLong(), noteId = noteId ?: -1)

        val notechecks = notePadUiState.checks.toMutableList()
        notechecks.add(noteCheck.toNoteCheckUiState())
    }
}