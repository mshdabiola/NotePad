package com.mshdabiola.selectlabelscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val labelRepository: ILabelRepository,
    private val notePadRepository: INotePadRepository,
) : ViewModel() {

    var labelScreenUiState by mutableStateOf(LabelScreenUiState())

    private var list: List<LabelUiState> = emptyList()
    private val labelsArgs = savedStateHandle.toRoute<LabelsArgs>()
    private val ids = labelsArgs.ids.split(",")
        .map { it.toLong() }
        .toSet()

    init {
        viewModelScope.launch {
        }
        viewModelScope.launch {
            updateList()
        }
    }

    fun onCheckClick(id: Long) {
        val labels = labelScreenUiState.labels.toMutableList()
        val index = labels.indexOfFirst { it.id == id }
        var label = labels[index]

        if (label.toggleableState == ToggleableState.Off || label.toggleableState == ToggleableState.Indeterminate) {
            label = label.copy(toggleableState = ToggleableState.On)
            labels[index] = label
            labelScreenUiState = labelScreenUiState.copy(labels = labels.toImmutableList())

            val labelsList = ids.map { NoteLabel(noteId = it, labelId = label.id) }
            viewModelScope.launch {
                labelRepository.upsertNoteLabel(labelsList)
            }
        } else {
            label = label.copy(toggleableState = ToggleableState.Off)
            labels[index] = label
            labelScreenUiState = labelScreenUiState.copy(labels = labels.toImmutableList())

            viewModelScope.launch {
                labelRepository.deleteNoteLabel(ids, label.id)
            }
        }
    }

    fun onSearchChange(text: String) {
        if (text.isBlank()) {
            labelScreenUiState =
                labelScreenUiState.copy(
                    editText = text,
                )
            viewModelScope.launch {
                updateList()
            }
        } else {
            val labels = list.filter { it.label.contains(text) }

            val haveSameText = list.any { it.label == text }
            labelScreenUiState =
                labelScreenUiState.copy(
                    editText = text,
                    labels = labels.toImmutableList(),
                    showAddLabel = haveSameText.not(),
                )
        }
    }

    private suspend fun updateList() {
        val labelsCount = ids.map {
            notePadRepository.getOneNotePad(it).first()!!.labels
        }
            .flatten().groupingBy { it.id }.eachCount()

        val labels = labelRepository.getAllLabels().first().map {
            val state = when (labelsCount[it.id]) {
                ids.size -> ToggleableState.On
                null -> ToggleableState.Off
                else -> ToggleableState.Indeterminate
            }
            it.toLabelUiState().copy(toggleableState = state)
        }
        list = labels

        labelScreenUiState = labelScreenUiState.copy(
            showAddLabel = false,
            labels = labels.toImmutableList(),
            editText = "",
        )
    }

    fun onCreateLabel() {
        viewModelScope.launch {
            val id = (list.lastOrNull()?.id ?: -1) + 1
            labelRepository.upsert(listOf(Label(id, labelScreenUiState.editText)))
            updateList()
            onCheckClick(id)
        }
    }
}
