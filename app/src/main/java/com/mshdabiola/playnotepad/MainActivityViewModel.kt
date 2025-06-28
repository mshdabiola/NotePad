/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val labelRepository: LabelRepository,
    private val addNoteUseCase: AddAllNoteUseCase,
    private val contentManager: IContentManager,

) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userDataRepository
        .userData.map {
            MainActivityUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(),
        )

    val labels = labelRepository
        .getAll().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    suspend fun insertNewNote(): Long {
        return addNoteUseCase(NotePad())
    }

    suspend fun insertNewAudioNote(uri: String, text: String): Long {
        val id = contentManager.saveVoice(uri)

        val voice = NoteVoice(
            id = id,
        )

        val notePad = NotePad(
            note = Note(detail = text),
            voices = listOf(voice),
        )
        return addNoteUseCase(notePad)
    }

    suspend fun insertNewImageNote(uri: String): Long {
        val id = contentManager.saveImage(uri)

        val image = NoteImage(
            id = id,
        )

        val notePad = NotePad(
            images = listOf(image),
        )
        return addNoteUseCase(notePad)
    }
    suspend fun insertNewDrawing(): Long {
        val notePad = NotePad()

        val noteId = addNoteUseCase(notePad)

        return noteId
    }
    suspend fun insertNewCheckNote(): Long {
        val notePad = NotePad(
            note = Note(isCheck = true),
            checks = listOf(NoteCheck()),
        )
        return addNoteUseCase(notePad)
    }

    fun pictureUri(): String {
        return contentManager.pictureUri()
    }

    fun setMainData(noteDisplayCategory: NoteDisplayCategory) {
        viewModelScope.launch {
            userDataRepository.setMainData(noteDisplayCategory)
        }
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}
