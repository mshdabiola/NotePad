/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SharedActivityViewModel @Inject constructor(
    private val notePadRepository: INotePadRepository,
    private val labelRepository: ILabelRepository,

) : ViewModel() {

    private val _state = MutableStateFlow<SharedActivityUiState>(SharedActivityUiState.Loading)
    val state = _state.asStateFlow()

    val title = TextFieldState()
    val content = TextFieldState()

    init {
        viewModelScope.launch {
            launch {
                snapshotFlow { content.text }
                    .debounce(500)
                    .collectLatest { text ->
                        _state.update {
                            val success = it

                            if (success is SharedActivityUiState.Success) {
                                success.copy(notepad = success.notepad.copy(detail = text.toString()))
                            } else {
                                it
                            }
                        }
                        save()
                    }
            }
            launch {
                snapshotFlow { title.text }
                    .debounce(500)
                    .collectLatest { text ->
                        _state.update {
                            val success = it

                            if (success is SharedActivityUiState.Success) {
                                success.copy(notepad = success.notepad.copy(title = text.toString()))
                            } else {
                                it
                            }
                        }
                        save()
                    }
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val st = state.value
            if (st is SharedActivityUiState.Success) {
                notePadRepository.upsert(st.notepad)
            }
        }
    }

    fun toggleCheck(index: Int) {
        val success = state.value as SharedActivityUiState.Success
        val label = success.labels[index]
        val labels = success.notepad.labels.toMutableList()

        if (labels.contains(label)) {
            labels.remove(label)
        } else {
            labels.add(label)
        }
        _state.update {
            success.copy(notepad = success.notepad.copy(labels = labels))
        }
        save()
    }

    fun newSharePost(title1: String, subject2: String, images: List<String>) {
        viewModelScope.launch {
            println("images $images, title $title1, subject $subject2")

            val labels = async {
                labelRepository.getAllLabels().first()
            }

            title.edit {
                this.append(title1)
            }
            content.edit {
                append(subject2)
            }

            val noteImage = images
                .map { notePadRepository.saveImage(it) }
                .map { NoteImage(id = it) }

            val notePad = NotePad(
                title = title1,
                detail = subject2,
                images = noteImage,
            )
            val id = notePadRepository.upsert(notePad)

            val newNote = notePadRepository.getOneNotePad(id).first()!!
            _state.update {
                SharedActivityUiState.Success(
                    labels = labels.await(),
                    notepad = newNote,
                )
            }
        }
    }
}

sealed interface SharedActivityUiState {
    data object Loading : SharedActivityUiState
    data class Success(
        val userData: UserData = UserData(
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.LIGHT,
            useDynamicColor = false,
            shouldHideOnboarding = false,
            contrast = Contrast.High,
        ),
        val notepad: NotePad = NotePad(),
        val labels: List<Label> = emptyList(),
    ) : SharedActivityUiState
}
