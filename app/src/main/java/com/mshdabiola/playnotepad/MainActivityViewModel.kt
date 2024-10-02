/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
    private val notePadRepository: INotePadRepository,
    private val labelRepository: ILabelRepository,

) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userDataRepository
        .userData.map {
            MainActivityUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    val labels = labelRepository
        .getAllLabels().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000),
        )

    suspend fun insertNewNote(): Long {
        return notePadRepository.upsert(NotePad())
    }

    suspend fun insertNewAudioNote(uri: String, text: String): Long {
        val id = notePadRepository.saveImage(uri)

        val voice = NoteVoice(
            id = id,
        )

        val notePad = NotePad(
            detail = text,
            voices = listOf(voice),
        )
        return notePadRepository.upsert(notePad)
    }

    suspend fun insertNewImageNote(uri: String): Long {
        val id = notePadRepository.saveImage(uri)

        val image = NoteImage(
            id = id,
        )

        val notePad = NotePad(
            images = listOf(image),
        )
        return notePadRepository.upsert(notePad)
    }
    suspend fun insertNewDrawing(): Pair<Long, Long> {
        val drawing = NoteImage(
            id = System.currentTimeMillis(),
            isDrawing = true,
        )

        val notePad = NotePad(
            images = listOf(drawing),
        )

        val noteId = notePadRepository.upsert(notePad)

        return Pair(noteId, drawing.id)
    }
    suspend fun insertNewCheckNote(): Long {
        val notePad = NotePad(
            isCheck = true,
            checks = listOf(NoteCheck()),
        )
        return notePadRepository.upsert(notePad)
    }

    fun pictureUri(): String {
        return notePadRepository.getUri()
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}
