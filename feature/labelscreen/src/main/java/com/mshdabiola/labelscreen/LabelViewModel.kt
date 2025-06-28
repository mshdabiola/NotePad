package com.mshdabiola.labelscreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LabelViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val labelRepository: LabelRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val labelArg = savedStateHandle.toRoute<LabelArg>()
    private val newLabel = MutableStateFlow(LabelState())

    val labels = labelRepository
        .getAll()

    val labelUiState = combine(
        labels,
        newLabel,
    ) { labels, newLabel ->
        LabelUiState(
            labels = labels.map { it.toLabelState() }.toImmutableList(),
            newLabel = newLabel,
            isEditMode = labelArg.isEditMode,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = LabelUiState(),
        )

    fun onAddNew(index: Int) {
        viewModelScope.launch {
            if (index == -1) {
                newLabel.value = LabelState()
                labelRepository.upsert(labelUiState.value.newLabel.toLabel())
            } else {
                labelRepository.upsert(labelUiState.value.labels[index].toLabel())
            }
        }
    }

    fun onDelete(id: Long) {
        viewModelScope.launch {
            val noteDisplayCategory = userDataRepository.userData.first().noteDisplayCategory
            if (noteDisplayCategory.noteType == NoteType.LABEL && noteDisplayCategory.labelId == id) {
                userDataRepository.setMainData(NoteDisplayCategory())
            }
            labelRepository.delete(id)
        }
    }
}
