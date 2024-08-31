/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IContentManager
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
    val contentManager: IContentManager,

) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userDataRepository
        .userData.map {
            MainActivityUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    suspend fun insertNewNote(): Long {
        return notePadRepository.upsert(NotePad(title = "abila", detail = "testing"))
    }

    suspend fun insertNewAudioNote(uri: Uri, text: String): Long {
        val id = contentManager.saveVoice(uri)

        val voice = NoteVoice(
            id = id,
        )

        val notePad = NotePad(
            detail = text,
            voices = listOf(voice),
        )
        return notePadRepository.upsert(notePad)
    }

    suspend fun insertNewImageNote(uri: Uri): Long {
        val id = contentManager.saveImage(uri)

        val image = NoteImage(
            id = id,
        )

        val notePad = NotePad(
            images = listOf(image),
        )
        return notePadRepository.upsert(notePad)
    }
    suspend fun insertNewCheckNote(): Long {
        val notePad = NotePad(
            isCheck = true,
            checks = listOf(NoteCheck()),
        )
        return notePadRepository.upsert(notePad)
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}
