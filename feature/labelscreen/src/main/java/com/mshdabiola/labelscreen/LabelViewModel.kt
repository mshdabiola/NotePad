package com.mshdabiola.labelscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NoteLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository
) : ViewModel() {

    var labelScreenUiState by mutableStateOf(LabelScreenUiState())
    private val labelArg = LabelArg(savedStateHandle)

    init {

        viewModelScope.launch {

            val list =
                labelRepository.getOneLabelList().map { it.toLabelUiState() }.toImmutableList()

            labelScreenUiState =
                labelScreenUiState.copy(labels = list, isEditMode = labelArg.editMode)
        }
        viewModelScope.launch {
            snapshotFlow { labelScreenUiState }
                .map { it.labels }
                .collectLatest { labelUiStates ->
                    labelRepository.upsert(labelUiStates.map { it.toLabel() })
                }
        }
    }


    fun onLabelChange(value: String, id: Long) {
        val labels = labelScreenUiState.labels.toMutableList()
        val index = labels.indexOfFirst { it.id == id }
        val labelUiState = labels[index].copy(label = value)
        labels[index] = labelUiState
        labelScreenUiState = labelScreenUiState.copy(labels = labels.toImmutableList())
    }

    fun onDelete(id: Long) {
        val labels = labelScreenUiState.labels.toMutableList()
        val index = labels.indexOfFirst { it.id == id }
        labels.removeAt(index)
        labelScreenUiState = labelScreenUiState.copy(labels = labels.toImmutableList())
        viewModelScope.launch {
            labelRepository.delete(id)
            noteLabelRepository.deleteByLabelId(id)
        }

    }

    fun onAddLabelChange(value: String) {

        labelScreenUiState = labelScreenUiState.copy(editText = value)

    }

    fun onAddLabelDone() {
        val labels = labelScreenUiState.labels.toMutableList()

        if (labels.any { it.label == labelScreenUiState.editText }) {
            labelScreenUiState = labelScreenUiState.copy(errorOccur = true)
        } else {
            val nextId = (labels.lastOrNull()?.id ?: 0) + 1
            val labelUiState = LabelUiState(id = nextId, label = labelScreenUiState.editText)
            labels.add(labelUiState)
            labelScreenUiState = labelScreenUiState.copy(
                labels = labels.toImmutableList(),
                editText = "",
                errorOccur = false
            )
        }

    }

    fun onAddDeleteValue() {
        labelScreenUiState = labelScreenUiState.copy(editText = "")
    }

}