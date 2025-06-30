package com.mshdabiola.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
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
//    savedStateHandle: SavedStateHandle,
//    private val notepadpadRepository: INotePadRepository,
//    private val alarmManager: IAlarmManager,
    private val noteRepository: NoteRepository,
    private val userDataRepository: UserDataRepository,
    private val labelRepository: LabelRepository,
    private val getAllNoteUseCase: GetAllNoteUseCase,
    private val addAllNoteUseCase: AddAllNoteUseCase,
) : ViewModel() {

    private val selectedNotesState = MutableStateFlow<SelectState?>(null)
    private val currentNotepads = userDataRepository
        .userData
        .mapLatest { it.noteDisplayCategory }
        .flatMapLatest {
            getAllNoteUseCase.invoke(it)
        }
    private val label = userDataRepository
        .userData
        .mapLatest { it.noteDisplayCategory }
        .flatMapLatest {
            labelRepository.get(it.labelId)
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

        val pinNote = notepad.filter { it.note.isPin }
        val unPinNote = notepad.filter { !it.note.isPin }
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
            val notepad = getAllNotePad().single { it.note.id == setOfSelected.first() }
            colorIndex = notepad.note.color
            notificationUiState = notepad.notification
        }

        val isAllPin = getAllNotePad()
            .filter { setOfSelected.contains(it.note.id) }
            .all { it.note.isPin }

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
            getAllNotePad().filter { selected.contains(it.note.id) }

        deselectNotes()

        if (selectedNotepad.any { !it.note.isPin }) {
            val pinNotepad = selectedNotepad.map {
                it.copy(note = it.note.copy(isPin = true))
            }

            viewModelScope.launch {
                for (note in pinNotepad) {
                    addAllNoteUseCase(note)
                }
            }
        } else {
            val unPinNote = selectedNotepad.map { it.copy(note = it.note.copy(isPin = false)) }

            viewModelScope.launch {
                for (note in unPinNote) {
                    addAllNoteUseCase(note)
                }
            }
        }
    }

    fun setAllColor(colorId: Int) {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.note.id) }

        deselectNotes()
        val notepads = selectedNotes.map { it.copy(note = it.note.copy(color = colorId)) }

        viewModelScope.launch {
            for (note in notepads) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onArchiveNote() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.note.id) }

        deselectNotes()
        val notepads = selectedNotes.map {
            val notepadType = if (it.note.noteType == NoteType.ARCHIVE) NoteType.NOTE else NoteType.ARCHIVE
            it.copy(note = it.note.copy(noteType = notepadType))
        }

        viewModelScope.launch {
            for (note in notepads) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onDeleteNote() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.note.id) }

        deselectNotes()
        val notepads = selectedNotes.map { it.copy(note = it.note.copy(noteType = NoteType.TRASH, isPin = false)) }

        viewModelScope.launch {
            for (note in notepads) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onDeleteForever() {
        val selected = getSelectState().setOfSelected

        deselectNotes()

        viewModelScope.launch {
            noteRepository.deleteIds(selected)
        }
    }
    fun onRestore() {
        val selected = getSelectState().setOfSelected
        val selectedNotes =
            getAllNotePad().filter { selected.contains(it.note.id) }
                .map { it.copy(note = it.note.copy(noteType = NoteType.NOTE)) }

        deselectNotes()

        viewModelScope.launch {
            for (note in selectedNotes) {
                addAllNoteUseCase(note)
            }
        }
    }

    fun onCopyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = getSelectState().setOfSelected.first()
            val notepads = getAllNotePad().find { it.note.id == id }

            deselectNotes()

            if (notepads != null) {
                val copy = notepads.copy(note = notepads.note.copy(id = -1))

                addAllNoteUseCase(copy)
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
            labelRepository.upserts(listOf(Label(labelId, name)))
        }
    }

    fun onDeleteAllTrash() {
        viewModelScope.launch {
            noteRepository.deleteTrash()
        }
    }

    // Todo("deleteByNoteId empty notepad")
//    fun deleteEmptyNote() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val emptyList = notepadpadRepository.getNotePads().first()
//                .filter { it.note.isEmpty() }
//
//            if (emptyList.isNotEmpty()) {
//                notepadpadRepository.deleteNotePad(emptyList)
//            }
//        }
//    }

    fun onDisplayModeChange() {
        viewModelScope.launch {
            userDataRepository.toggleGrid()
        }
    }

    fun setAlarm(notificationUiState: NotificationUiState) {
//        val time = timeListDefault[dateTimeState.value.currentTime]
//        val date = when (dateTimeState.value.currentDate) {
//            0 -> today.date
//            1 -> today.date.plus(1, DateTimeUnit.note.DAY)
//            else -> currentLocalDate
//        }
//        val interval = when (dateTimeState.value.currentInterval) {
//            0 -> null
//            1 -> DateTimeUnit.note.HOUR.times(24).duration.toLong(DurationUnit.note.MILLISECONDS)
//
//            2 -> DateTimeUnit.note.HOUR.times(24 * 7).duration.toLong(DurationUnit.note.MILLISECONDS)
//
//            3 -> DateTimeUnit.note.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.note.MILLISECONDS)
//
//            else -> DateTimeUnit.note.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.note.MILLISECONDS)
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
    }

    fun onDeleteAlarm() {
    }

    private fun getSuccess() = mainState.value as MainState.Success
    fun onSendNote(): NotePad {
        val notepad = getAllNotePad().first { it.note.id == getSelectState().setOfSelected.first() }
        deselectNotes()
        return notepad
    }
}
