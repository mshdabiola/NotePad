package com.mshdabiola.selectlabelscreen

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val labelRepository: ILabelRepository,
    private val notePadRepository: INotePadRepository,
) : ViewModel() {

    private val labelsArgs = savedStateHandle.toRoute<LabelsArgs>()
    private val ids = labelsArgs.ids.split(",")
        .map { it.toLong() }
        .toSet()

    private val notePadLabels = notePadRepository
        .getNotePadsByIds(ids)
        .mapLatest { note -> note.map { it.labels } }
    private val labels = labelRepository
        .getAllLabels()
    private val initLabelState = LabelUiState()

    @OptIn(FlowPreview::class)
    val labelUiState = combine(
        snapshotFlow { initLabelState.labelQuery.text }
            .debounce(500),
        notePadLabels,
        labels,
    ) { query, notePadLabels, labels ->
        val labelsCount = notePadLabels
            .flatten().groupingBy { it.id }.eachCount()
        val labelStates = labels.map {
            val state = when (labelsCount[it.id]) {
                ids.size -> ToggleableState.On
                null -> ToggleableState.Off
                else -> ToggleableState.Indeterminate
            }
            LabelState(it.id, it.label, state)
        }
        var showAddLabel = false
        val list = if (query.isBlank()) {
            labelStates
        } else {
            showAddLabel = labels.any { it.label != query }
            labelStates.filter { it.label.contains(query) }
        }

        LabelUiState(list, initLabelState.labelQuery, showAddLabel)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = initLabelState,
        )

    fun onCheckClick(index: Int) {
        val labels = labelUiState.value.labels
        var label = labels[index]

        if (label.toggleableState == ToggleableState.Off || label.toggleableState == ToggleableState.Indeterminate) {
            label = label.copy(toggleableState = ToggleableState.On)
            val labelsList = ids.map { NoteLabel(noteId = it, labelId = label.id) }
            viewModelScope.launch {
                labelRepository.upsertNoteLabel(labelsList)
            }
        } else {
            label = label.copy(toggleableState = ToggleableState.Off)

            viewModelScope.launch {
                labelRepository.deleteNoteLabel(ids, label.id)
            }
        }
    }

//    fun onSearchChange(text: String) {
//        if (text.isBlank()) {
//            labelScreenUiState =
//                labelScreenUiState.copy(
//                    editText = text,
//                )
//            viewModelScope.launch {
//                updateList()
//            }
//        } else {
//            val labels = list.filter { it.label.contains(text) }
//
//            val haveSameText = list.any { it.label == text }
//            labelScreenUiState =
//                labelScreenUiState.copy(
//                    editText = text,
//                    labels = labels.toImmutableList(),
//                    showAddLabel = haveSameText.not(),
//                )
//        }
//    }
//
//    private suspend fun updateList() {
//        val labelsCount = ids.map {
//            notePadRepository.getOneNotePad(it).first()!!.labels
//        }
//            .flatten().groupingBy { it.id }.eachCount()
//
//        val labels = labelRepository.getAllLabels().first().map {
//            val state = when (labelsCount[it.id]) {
//                ids.size -> ToggleableState.On
//                null -> ToggleableState.Off
//                else -> ToggleableState.Indeterminate
//            }
//            it.toLabelUiState().copy(toggleableState = state)
//        }
//        list = labels
//
//        labelScreenUiState = labelScreenUiState.copy(
//            showAddLabel = false,
//            labels = labels.toImmutableList(),
//            editText = "",
//        )
//    }

    fun onCreateLabel() {
        viewModelScope.launch {
            val label = Label(
                -1,
                labelUiState.value.labelQuery.text.toString(),
            )
            labelUiState.value.labelQuery.clearText()

            val noteIds = labelRepository.upsert(
                listOf(
                    label,
                ),
            )
            val labelsList = ids.map { NoteLabel(noteId = it, labelId = noteIds[0]) }
            labelRepository.upsertNoteLabel(labelsList)
        }
    }
}
