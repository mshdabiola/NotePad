package com.mshdabiola.mainscreen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.AlarmManager
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.database.repository.NoteRepository
import com.mshdabiola.designsystem.component.state.NoteType
import com.mshdabiola.designsystem.component.state.toLabelUiState
import com.mshdabiola.designsystem.component.state.toNotePad
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import com.mshdabiola.designsystem.component.state.toNoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notepadRepository: NotePadRepository,
    private val contentManager: ContentManager,
    private val labelRepository: LabelRepository,
    private val noteRepository: NoteRepository,
    private val alarmManager: AlarmManager
) : ViewModel() {


    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    init {


        viewModelScope.launch {
            combine(
                labelRepository.getAllLabels(),
                mainState.map { it.noteType },
                transform = { T1, T2 ->

                    Pair(T1, T2)
                })
                .distinctUntilChanged { old, new -> old == new }
                .collectLatest { pair ->

                    _mainState.value = mainState.value.copy(
                        labels = pair.first.map { it.toLabelUiState() }.toImmutableList()
                    )

                    when (pair.second) {
                        is NoteType.LABEL -> {
                            notepadRepository.getNotePads().map { notes ->
                                notes.filter { it.labels.any { it.labelId == (pair.second as NoteType.LABEL).index } }
                                    .map { it.toNotePadUiState(pair.first) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList()
                                    )
                                }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }

                        is NoteType.REMAINDER -> {
                            notepadRepository.getNotePads().map { notes ->
                                notes.map { it.toNotePadUiState(pair.first) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.filter { it.note.reminder > 0 }.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList()
                                    )
                                }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }

                        else -> {
                            notepadRepository.getNotePads(pair.second.toNoteType()).map { notes ->
                                notes.map { it.toNotePadUiState(pair.first) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList()
                                    )
                                }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }
                    }
                }


        }
    }

    fun onSelectCard(id: Long) {
        var listNOtePad = mainState.value.notePads.toMutableList()
        val index = listNOtePad.indexOfFirst { it.note.id == id }
        val notepad = listNOtePad[index]
        val newNotepad = notepad.copy(note = notepad.note.copy(selected = !notepad.note.selected))

        listNOtePad[index] = newNotepad

        _mainState.value = mainState.value.copy(notePads = listNOtePad.toImmutableList())


    }

    fun clearSelected() {
        val listNOtePad =
            mainState.value.notePads.map { it.copy(note = it.note.copy(selected = false)) }
        _mainState.value = mainState.value.copy(notePads = listNOtePad.toImmutableList())
    }

    fun savePhoto(uri: Uri, id: Long) {
        viewModelScope.launch {
            contentManager.saveImage(uri, id)
        }
    }

    fun saveVoice(uri: Uri, id: Long) {
        viewModelScope.launch {
            contentManager.saveVoice(uri, id)
        }
    }

    fun getPhotoUri(id: Long): Uri {
        return contentManager.pictureUri(id)
    }

    fun setNoteType(noteType: NoteType) {
        _mainState.value = mainState.value.copy(noteType = noteType)
    }

    fun setPin() {
        val selectedNotepad = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad() }

        clearSelected()


        if (selectedNotepad.any { !it.note.isPin }) {
            val pinNotepad = selectedNotepad.map { it.note.copy(isPin = true) }

            viewModelScope.launch {
                noteRepository.upsert(pinNotepad)
            }
        } else {
            val unPinNote = selectedNotepad.map { it.note.copy(isPin = false) }

            viewModelScope.launch {
                noteRepository.upsert(unPinNote)
            }
        }

    }

    fun setAlarm(time: Long, interval: Long?) {
        val selectedNotes = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad().note }

        clearSelected()
        val notes = selectedNotes.map { it.copy(reminder = time, interval = interval ?: -1) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.setAlarm(
                    time,
                    interval,
                    requestCode = it.id?.toInt() ?: -1,
                    title = it.title,
                    content = it.detail,
                    noteId = it.id ?: 0L
                )
            }


        }

    }

    fun deleteAlarm() {

        val selectedNotes = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad().note }

        clearSelected()
        val notes = selectedNotes.map { it.copy(reminder = -1, interval = -1) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.deleteAlarm(it.id?.toInt() ?: 0)
            }

        }
    }

    fun setAllColor(colorId: Int) {

        val selectedNotes = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad().note }

        clearSelected()
        val notes = selectedNotes.map { it.copy(color = colorId) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }

    }

    fun setAllArchive() {

        val selectedNotes = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad().note }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = com.mshdabiola.model.NoteType.ARCHIVE) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }

    }

    fun setAllDelete() {

        val selectedNotes = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad().note }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = com.mshdabiola.model.NoteType.TRASH) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }

    }

    fun copyNote() {
        viewModelScope.launch {
            val id = mainState.value.notePads.single { it.note.selected }.note.id!!
            val notepads = notepadRepository.getOneNotePad(id)

            var copy = notepads.copy(note = notepads.note.copy(id = null))

            val newId = notepadRepository.insertNotepad(copy)

            copy = copy.copy(
                note = copy.note.copy(id = newId),
                images = copy.images.map { it.copy(noteId = newId) },
                voices = copy.voices.map { it.copy(noteId = newId) },
                labels = copy.labels.map { it.copy(noteId = newId) },
                checks = copy.checks.map { it.copy(noteId = newId) }
            )

            notepadRepository.insertNotepad(copy)


        }

    }


}