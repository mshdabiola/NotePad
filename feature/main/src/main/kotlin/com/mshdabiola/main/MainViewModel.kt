package com.mshdabiola.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class MainViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notepadRepository: INotePadRepository,
    private val alarmManager: IAlarmManager,
    private val userDataRepository: UserDataRepository,
    private val labelRepository: ILabelRepository,
) : ViewModel() {

    private val selectedNotesState = MutableStateFlow<SelectState?>(null)
    private val currentNotepads = userDataRepository
        .userData
        .mapLatest { it.noteDisplayCategory }
        .flatMapLatest {
            notepadRepository.getNotePadsWithMainData(it)
        }
    private val label = userDataRepository
        .userData
        .mapLatest { it.noteDisplayCategory }
        .flatMapLatest {
            labelRepository.getLabel(it.labelId)
        }
    private val noteDisplayCategory = userDataRepository
        .userData
        .mapLatest { it.noteDisplayCategory }

    private val isGrid = userDataRepository
        .userData
        .mapLatest { it.isGrid }
    val mainState = combine(
        currentNotepads,
        label,
        noteDisplayCategory,
        selectedNotesState,
        isGrid,
    ) { notepad, label, displayCategory, selectState, isGrid ->

        val pinNote = notepad.filter { it.isPin }
        val unPinNote = notepad.filter { !it.isPin }
        MainState.Success(
            labelName = label?.label,
            pinNotePads = pinNote,
            unPinNotePads = unPinNote,
            noteDisplayCategory = displayCategory,
            selectState = selectState,
            isGrid = isGrid,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MainState.Loading,
    )

    private fun getSelectState(): SelectState {
        return selectedNotesState.value ?: SelectState()
    }

    private fun getAllNotePad(): List<NotePad> {
        return getSuccess().unPinNotePads + getSuccess().pinNotePads
    }

    fun handleCardSelection(id: Long) {
        val state = getSelectState()

        if (state.setOfSelected.contains(id) && state.setOfSelected.size == 1) {
            deselectNotes()
            return
        }

        val setOfSelected = if (state.setOfSelected.contains(id)) {
            state.setOfSelected - id
        } else {
            state.setOfSelected + id
        }
        var notificationUiState: NotificationUiState? = null
        var colorIndex = -1
        if (setOfSelected.size == 1) {
            val note = getAllNotePad().single { it.id == setOfSelected.first() }
            colorIndex = note.background
            notificationUiState = note.notification
        }

        val isAllPin = getAllNotePad()
            .filter { setOfSelected.contains(it.id) }
            .all { it.isPin }

        selectedNotesState.value = state.copy(
            setOfSelected = setOfSelected,
            isAllPin = isAllPin,
            colorIndex = colorIndex,
            notificationUiState = notificationUiState,
        )
    }

    fun deselectNotes() {
        selectedNotesState.value = null
    }

    fun pinOrUnpinNotes() {
        val selected = getSelectState().setOfSelected
        val selectedNotepad =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()

        if (selectedNotepad.any { !it.isPin }) {
            val pinNotepad = selectedNotepad.map { it.copy(isPin = true) }

            viewModelScope.launch {
                notepadRepository.upsert(pinNotepad)
            }
        } else {
            val unPinNote = selectedNotepad.map { it.copy(isPin = false) }

            viewModelScope.launch {
                notepadRepository.upsert(unPinNote)
            }
        }
    }

    fun setAllColor(colorId: Int) {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notes = selectedNotes.map { it.copy(color = colorId) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun onArchiveNote() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.ARCHIVE) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun onDeleteNote() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.TRASH) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun onCopyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = getSelectState().setOfSelected.first()
            val notepads = notepadRepository.getOneNotePad(id).first()

            deselectNotes()

            if (notepads != null) {
                val copy = notepads.copy(id = -1)

                notepadRepository.upsert(copy)
            }
        }
    }

    fun deleteLabel() {
        val labelId = getSuccess().noteDisplayCategory.labelId

        viewModelScope.launch {
            userDataRepository.setMainData(NoteDisplayCategory(0, NoteType.NOTE))
            labelRepository.delete(labelId)
        }
    }

    fun renameLabel(name: String) {
        val labelId = getSuccess().noteDisplayCategory.labelId
//
        viewModelScope.launch {
            labelRepository.upsert(listOf(Label(labelId, name)))
        }
    }

    fun onDeleteAllTrash() {
        viewModelScope.launch {
            notepadRepository.deleteTrashType()
        }
    }

    // Todo("delete empty note")
    fun deleteEmptyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val emptyList = notepadRepository.getNotePads().first()
                .filter { it.isEmpty() }

            if (emptyList.isNotEmpty()) {
                notepadRepository.deleteNotePad(emptyList)
            }
        }
    }

    fun onDisplayModeChange() {
        viewModelScope.launch {
            userDataRepository.toggleGrid()
        }
    }

    fun setAlarm(notificationUiState: NotificationUiState) {
//        val time = timeListDefault[dateTimeState.value.currentTime]
//        val date = when (dateTimeState.value.currentDate) {
//            0 -> today.date
//            1 -> today.date.plus(1, DateTimeUnit.DAY)
//            else -> currentLocalDate
//        }
//        val interval = when (dateTimeState.value.currentInterval) {
//            0 -> null
//            1 -> DateTimeUnit.HOUR.times(24).duration.toLong(DurationUnit.MILLISECONDS)
//
//            2 -> DateTimeUnit.HOUR.times(24 * 7).duration.toLong(DurationUnit.MILLISECONDS)
//
//            3 -> DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)
//
//            else -> DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)
//        }
//
//        val setime = LocalDateTime(date, time)
//        if (setime > today) {
//            setAlarm(
//                setime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
//                interval,
//            )
//            Log.e("editv", "Set Alarm")
//        } else {
//            Log.e("editv", "Alarm not set $today time $time date$date")
//        }
    }

    private fun setAlarm(time: Long, interval: Long?) {
        val setOfSelected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { setOfSelected.contains(it.id) }

        deselectNotes()
        val notes = selectedNotes // .map { it.copy(reminder = time, interval = interval ?: -1) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.setAlarm(
                    time,
                    interval,
                    requestCode = it.id?.toInt() ?: -1,
                    title = it.title,
                    content = it.detail,
                    noteId = it.id ?: 0L,
                )
            }
        }
    }

    fun onDeleteAlarm() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.id) }

        deselectNotes()
        val notes = selectedNotes // .map { it.copy(reminder = -1, interval = -1) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.deleteAlarm(it.id?.toInt() ?: 0)
            }
        }
    }

    private fun getSuccess() = mainState.value as MainState.Success
    fun onSendNote(): NotePad {
        val note = getAllNotePad().first { it.id == getSelectState().setOfSelected.first() }
        deselectNotes()
        return note
    }
}
