package com.mshdabiola.selectlabel

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.selectlabel.navigation.SelectLabelsArgs
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SelectLabelViewModel.Factory::class)
class SelectLabelViewModel @AssistedInject constructor(
    @Assisted val selectLabelsArgs: SelectLabelsArgs,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
) : ViewModel() {

    private val ids = selectLabelsArgs.ids.split(",")
        .map { it.toLong() }
        .toSet()

    private val notePadLabels = noteLabelRepository
        .getByNoteIds(ids)
    private val labels = labelRepository
        .getAll()
    private val initLabelState = SelectLabelUiState()

    @OptIn(FlowPreview::class)
    val selectLabelUiState = combine(
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

        SelectLabelUiState(list, initLabelState.labelQuery, showAddLabel)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = initLabelState,
        )

    fun onCheckClick(index: Int) {
        val labels = selectLabelUiState.value.labels
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
                selectLabelUiState.value.labelQuery.text.toString(),
            )
            selectLabelUiState.value.labelQuery.clearText()

            val noteId = labelRepository.upsert(
                label,

            )
            val labelsList = ids.map { NoteLabel(noteId = it, labelId = noteId) }
            noteLabelRepository.upserts(labelsList)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(selectLabelsArgs: SelectLabelsArgs): SelectLabelViewModel
    }
}
