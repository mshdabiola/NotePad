package com.mshdabiola.label

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.labelscreen.LabelArg
import com.mshdabiola.labelscreen.LabelState
import com.mshdabiola.labelscreen.LabelUiState
import com.mshdabiola.labelscreen.toLabel
import com.mshdabiola.labelscreen.toLabelState
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = LabelViewModel.Factory::class)
class LabelViewModel @AssistedInject constructor(
    @Assisted val labelArg: LabelArg,
    private val labelRepository: LabelRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

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
            launch {
                val noteDisplayCategory = async { userDataRepository.userData.first().noteDisplayCategory }
                if (noteDisplayCategory.await().noteType == NoteType.LABEL && noteDisplayCategory.await().labelId == id) {
                    userDataRepository.setNoteDisplayCategory(NoteDisplayCategory())
                }
            }
            launch {
                labelRepository.delete(id)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(labelArg: LabelArg): LabelViewModel
    }
}
