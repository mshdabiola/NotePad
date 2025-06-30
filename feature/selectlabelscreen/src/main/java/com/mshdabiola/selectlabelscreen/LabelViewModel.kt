package com.mshdabiola.selectlabelscreen

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
) : ViewModel() {

    private val labelsArgs = savedStateHandle.toRoute<LabelsArgs>()
    private val ids = labelsArgs.ids.split(",")
        .map { it.toLong() }
        .toSet()

    private val notePadLabels = noteLabelRepository
        .getByNoteIds(ids)
    private val labels = labelRepository
        .getAll()
    private val initLabelState = LabelUiState()

    @OptIn(FlowPreview::class)
    val labelUiState = combine(
        snapshotFlow { initLabelState.labelQuery.text }
            .debounce(500),
        notePadLabels,
        labels,
    ) { query, notePadLabels, labels ->
        val labelsCount = notePadLabels
            .groupingBy { it.labelId }.eachCount()
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
                noteLabelRepository.upserts(labelsList)
            }
        } else {
            label = label.copy(toggleableState = ToggleableState.Off)

            viewModelScope.launch {
                ids.forEach {
                    noteLabelRepository.deleteByNoteIdAndLabelId(it, label.id)
                }
            }
        }
    }


    fun onCreateLabel() {
        viewModelScope.launch {
            val label = Label(
                -1,
                labelUiState.value.labelQuery.text.toString(),
            )
            labelUiState.value.labelQuery.clearText()

            val noteId = labelRepository.upsert(
                label,

            )
            val labelsList = ids.map { NoteLabel(noteId = it, labelId = noteId) }
            noteLabelRepository.upserts(labelsList)
        }
    }
}
