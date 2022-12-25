package com.mshdabiola.editscreen

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.common.NotePlayer
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.editscreen.state.NoteCheckUiState
import com.mshdabiola.editscreen.state.NotePadUiState
import com.mshdabiola.editscreen.state.toNoteCheckUiState
import com.mshdabiola.editscreen.state.toNoteImageUiState
import com.mshdabiola.editscreen.state.toNotePad
import com.mshdabiola.editscreen.state.toNotePadUiState
import com.mshdabiola.editscreen.state.toNoteVoiceUiState
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteVoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notePadRepository: NotePadRepository,
    private val contentManager: ContentManager,
    private val voicePlayer: NotePlayer

) : ViewModel() {

    private val editArg = EditArg(savedStateHandle)
    var notePadUiState by mutableStateOf(NotePad().toNotePadUiState())

    private var photoId: Long = 0


    init {
        viewModelScope.launch {
            Log.e("Editviewmodel", "${editArg.id} ${editArg.content} ${editArg.data}")
            notePadUiState = when (editArg.id) {
                (-1).toLong() -> NotePad().toNotePadUiState()
                (-2).toLong() -> NotePad(note = Note(isCheck = true)).toNotePadUiState()
                (-3).toLong() -> NotePad(
                    note = Note(detail = editArg.content), images = listOf(
                        NoteImage(0, -1, contentManager.getImagePath(editArg.data))
                    )
                ).toNotePadUiState()

                (-4).toLong() -> {

                    NotePad(
                        note = Note(detail = editArg.content), voices = listOf(
                            NoteVoice(0, -1, contentManager.getVoicePath(editArg.data))
                        )
                    ).toNotePadUiState()
                }

                else ->
                    notePadRepository.getOneNotePad(editArg.id).toNotePadUiState()
            }
        }

        viewModelScope.launch {
            snapshotFlow {
                notePadUiState
            }

                .distinctUntilChanged { old, new -> old == new }
                .collectLatest {
                    Log.e("flow", "$it")
                    insertNotePad(it)
                }
        }

    }


    private suspend fun insertNotePad(notePad: NotePadUiState) {


        if (notePad.note.title.isNotBlank()
            || notePad.note.detail.isNotBlank()
            || notePad.checks.isNotEmpty()
            || notePad.voices.isNotEmpty()
            || notePad.images.isNotEmpty()
        ) {
            if (notePad.note.id == null) {
                val id = notePadRepository.insertNotepad(notePad.toNotePad())
                savedStateHandle[noteId] = id
                val note = notePadUiState.note.copy(id = id)
                val notechecks = notePad.checks.toMutableList()
                if (notechecks.isNotEmpty()) {

                    notechecks[0] = notechecks[0].copy(noteId = id)
                }
                val noteImages = notePad.images.toMutableList()
                if (noteImages.isNotEmpty()) {

                    noteImages[0] = noteImages[0].copy(noteId = id)
                }
                val noteVoice = notePad.voices.toMutableList()
                if (noteVoice.isNotEmpty()) {

                    noteVoice[0] = noteVoice[0].copy(noteId = id)
                }

                notePadUiState = notePadUiState.copy(
                    note = note,
                    checks = notechecks.toImmutableList(),
                    images = noteImages.toImmutableList(),
                    voices = noteVoice.toImmutableList()
                )

                //noteState = noteUiState.copy(id = id)

            } else {
                notePadRepository.insertNotepad(notePad.toNotePad())

            }
        }

    }

    fun onTitleChange(title: String) {
        val note = notePadUiState.note.copy(title = title)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun onDetailChange(detail: String) {
        val note = notePadUiState.note.copy(detail = detail)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun addCheck() {
        val size = (notePadUiState.checks.lastOrNull()?.id ?: -1) + 1
        val noteId = notePadUiState.note.id
        val noteCheck = NoteCheck(id = size.toLong(), noteId = noteId ?: -1)

        val noteChecks = notePadUiState.checks.toMutableList()
        noteChecks.add(noteCheck.toNoteCheckUiState())
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheckChange(value: String, id: Long) {


        val noteChecks = notePadUiState.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(content = value)
        noteChecks[index] = noteCheck
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheck(check: Boolean, id: Long) {
        val noteChecks = notePadUiState.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(isCheck = check)
        noteChecks[index] = noteCheck
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheckDelete(id: Long) {
        val noteChecks = notePadUiState.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks.removeAt(index)
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
        viewModelScope.launch {
            notePadRepository.deleteCheckNote(id, noteCheck.noteId)
        }
    }

    fun playMusic(path: String) {
        voicePlayer.playMusic(path)
    }

    fun saveImage(uri: Uri, id: Long) {
        contentManager.saveImage(uri, id)

        val size = (notePadUiState.images.lastOrNull()?.id ?: -1) + 1
        val noteImage =
            NoteImage(size, notePadUiState.note.id ?: -1, contentManager.getImagePath(id))
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState())

        notePadUiState = notePadUiState.copy(images = listImage.toImmutableList())


    }

    fun saveVoice(uri: Uri, content: String, id: Long) {
        contentManager.saveVoice(uri, id)

        val size = (notePadUiState.voices.lastOrNull()?.id ?: -1) + 1
        val noteVoice =
            NoteVoice(size, notePadUiState.note.id ?: -1, contentManager.getVoicePath(id))
        val listVoices = notePadUiState.voices.toMutableList()
        listVoices.add(noteVoice.toNoteVoiceUiState())

        val note = notePadUiState.note.copy(detail = notePadUiState.note.detail + "\n" + content)



        notePadUiState = notePadUiState.copy(note = note, voices = listVoices.toImmutableList())


    }

    fun savePhoto() {

        val size = (notePadUiState.images.lastOrNull()?.id ?: -1) + 1
        val noteImage =
            NoteImage(size, notePadUiState.note.id ?: -1, contentManager.getImagePath(photoId))
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState())

        notePadUiState = notePadUiState.copy(images = listImage.toImmutableList())
    }

    fun getPhotoUri(): Uri {
        photoId = System.currentTimeMillis()
        return contentManager.pictureUri(photoId)
    }

    fun changeToCheckBoxes() {
        val newNote = notePadUiState.note.detail.split("\n")
        val id = notePadUiState.note.id ?: -1
        val noteChecks = newNote.mapIndexed { index, s ->
            NoteCheck(id = index.toLong(), noteId = id, content = s).toNoteCheckUiState()
        }
        notePadUiState = notePadUiState.copy(
            note = notePadUiState.note.copy(isCheck = true, detail = ""),
            checks = noteChecks.toImmutableList()
        )

    }

    fun unCheckAllItems() {
        val noteChecks = notePadUiState.checks.map { it.copy(isCheck = false) }

        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())

    }

    fun deleteCheckedItems() {
        val checkNote = notePadUiState.checks.filter { it.isCheck }
        val notCheckNote = notePadUiState.checks.filter { !it.isCheck }

        notePadUiState = notePadUiState.copy(checks = notCheckNote.toImmutableList())

        viewModelScope.launch {
            checkNote.forEach {
                notePadRepository.deleteCheckNote(it.id, it.noteId)
            }
        }

    }

    fun hideCheckBoxes() {

        val noteCheck = notePadUiState.checks.joinToString(separator = "\n") { it.content }

        notePadUiState = notePadUiState.copy(
            note = notePadUiState.note.copy(detail = noteCheck, isCheck = false),
            checks = emptyList<NoteCheckUiState>().toImmutableList()
        )

        viewModelScope.launch {
            notePadRepository.deleteNoteCheckByNoteId(notePadUiState.note.id!!)
        }

    }


}