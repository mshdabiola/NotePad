package com.mshdabiola.main

import android.util.Log
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.NoteType
import com.mshdabiola.ui.state.DateDialogUiData
import com.mshdabiola.ui.state.DateListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import kotlin.time.DurationUnit

@HiltViewModel
class MainViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notepadRepository: INotePadRepository,
    private val alarmManager: IAlarmManager,
) : ViewModel() {

    //  val id = savedStateHandle.toRoute<Main>().id
    private val _mainState = MutableStateFlow<MainState>(MainState.Loading)
    val mainState = _mainState.asStateFlow()

    init {

        viewModelScope.launch {
            notepadRepository.getNotePads().collectLatest {

                try {
                    _mainState.value = getSuccess().copy(
                        notePads = it,
                    )
                } catch (e: Exception) {
                    _mainState.value = MainState.Success(
                        notePads = it,
                    )
                }
                initDate()
            }
        }
    }

    fun onSelectCard(id: Long) {
        val listNOtePad = getSuccess().notePads.toMutableList()
        val index = listNOtePad.indexOfFirst { it.id == id }
        val notepad = listNOtePad[index]
        val newNotepad = notepad.copy(selected = !notepad.selected)

        listNOtePad[index] = newNotepad

        _mainState.value = getSuccess().copy(notePads = listNOtePad.toImmutableList())
    }

    fun clearSelected() {
        val listNOtePad =
            getSuccess().notePads.map { it.copy(selected = false) }
        _mainState.value = getSuccess().copy(notePads = listNOtePad.toImmutableList())
    }

    fun setNoteType(noteType: NoteType) {
        _mainState.value = MainState.Success(noteType = noteType)
    }

    fun setPin() {
        val selectedNotepad =
            getSuccess().notePads.filter { it.selected }

        clearSelected()

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

    private fun setAlarm(time: Long, interval: Long?) {
        val selectedNotes =
            getSuccess().notePads.filter { it.selected }

        clearSelected()
        val notes = selectedNotes.map { it.copy(reminder = time, interval = interval ?: -1) }

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

    fun deleteAlarm() {
        val selectedNotes =
            getSuccess().notePads.filter { it.selected }

        clearSelected()
        val notes = selectedNotes.map { it.copy(reminder = -1, interval = -1) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }

        viewModelScope.launch {
            notes.forEach {
                alarmManager.deleteAlarm(it.id?.toInt() ?: 0)
            }
        }
    }

    fun setAllColor(colorId: Int) {
        val selectedNotes =
            getSuccess().notePads.filter { it.selected }

        clearSelected()
        val notes = selectedNotes.map { it.copy(color = colorId) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun setAllArchive() {
        val selectedNotes =
            getSuccess().notePads.filter { it.selected }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.ARCHIVE) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun setAllDelete() {
        val selectedNotes =
            getSuccess().notePads.filter { it.selected }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.TRASH) }

        viewModelScope.launch {
            notepadRepository.upsert(notes)
        }
    }

    fun copyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = getSuccess().notePads.single { it.selected }.id
            val notepads = notepadRepository.getOneNotePad(id).first()

            if (notepads != null) {
                val copy = notepads.copy(id = -1)

                notepadRepository.upsert(copy)
            }
        }
    }

    fun deleteLabel() {
//        val labelId = (getSuccess().noteType).id
//
//        _mainState.value = getSuccess().copy(noteType = NoteTypeUi())
//
//        viewModelScope.launch {
//            labelRepository.delete(labelId)
//            // noteLabelRepository.deleteByLabelId(labelId)
//        }
    }

    fun renameLabel(name: String) {
//        val labelId = (getSuccess().noteType).id
//
//        viewModelScope.launch {
//            labelRepository.upsert(listOf(Label(labelId, name)))
//        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            notepadRepository.deleteTrashType()
        }
    }

    fun deleteEmptyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val emptyList = notepadRepository.getNotePads().first()
                .filter { it.isEmpty() }

            if (emptyList.isNotEmpty()) {
                notepadRepository.deleteNotePad(emptyList)
            }
        }
    }

    private val _dateTimeState = MutableStateFlow(DateDialogUiData())
    val dateTimeState = _dateTimeState.asStateFlow()
    private lateinit var currentDateTime: LocalDateTime
    private lateinit var today: LocalDateTime
    private val timeListDefault = mutableListOf(
        LocalTime(7, 0, 0),
        LocalTime(13, 0, 0),
        LocalTime(19, 0, 0),
        LocalTime(20, 0, 0),
        LocalTime(20, 0, 0),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    var datePicker: DatePickerState = DatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        locale = Locale.getDefault(),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    var timePicker: TimePickerState = TimePickerState(12, 4, is24Hour = false)
    private lateinit var currentLocalDate: LocalDate

    // date and time dialog logic

    private fun initDate() {
        val now = Clock.System.now()
        today = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val today2 =
            now.plus(10, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.currentSystemDefault())
        currentDateTime = today2
        currentLocalDate = currentDateTime.date
        Timber.tag("current date").e(currentLocalDate.toString())

        val timeList = mutableListOf(
            DateListUiState(
                title = "Morning",
                value = "7:00 AM",
                trail = "7:00 AM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Afternoon",
                value = "1:00 PM",
                trail = "1:00 PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Evening",
                value = "7:00 PM",
                trail = "7:00 PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Night",
                value = "8:00 PM",
                trail = "8:00 PM",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Pick time",
                value = "1:00 PM",
                isOpenDialog = true,
                enable = true,
            ),

        ).mapIndexed { index, dateListUiState ->
            if (index != timeListDefault.lastIndex) {
                val greater = timeListDefault[index] > today.time
                dateListUiState.copy(
                    enable = greater,
                    value = notepadRepository.timeToString(timeListDefault[index]),
                    trail = notepadRepository.timeToString(timeListDefault[index]),
                )
            } else {
                timeListDefault[timeListDefault.lastIndex] = currentDateTime.time
                dateListUiState.copy(value = notepadRepository.timeToString(currentDateTime.time))
            }
        }.toImmutableList()
        val datelist = listOf(
            DateListUiState(
                title = "Today",
                value = "Today",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Tomorrow",
                value = "Tomorrow",
                isOpenDialog = false,
                enable = true,
            ),
            DateListUiState(
                title = "Pick date",
                value = notepadRepository.dateToString(currentDateTime.date),
                isOpenDialog = true,
                enable = true,
            ),
        ).toImmutableList()
        val interval = 0

        _dateTimeState.update {
            it.copy(
                isEdit = false,
                currentTime = timeList.lastIndex,
                timeData = timeList,
                timeError = today > currentDateTime,
                currentDate = 0,
                dateData = datelist,
                currentInterval = interval,
                interval = listOf(
                    DateListUiState(
                        title = "Does not repeat",
                        value = "Does not repeat",
                        isOpenDialog = false,
                        enable = true,
                    ),
                    DateListUiState(
                        title = "Daily",
                        value = "Daily",
                        isOpenDialog = false,
                        enable = true,
                    ),
                    DateListUiState(
                        title = "Weekly",
                        value = "Weekly",
                        isOpenDialog = false,
                        enable = true,
                    ),
                    DateListUiState(
                        title = "Monthly",
                        value = "Monthly",
                        isOpenDialog = false,
                        enable = true,
                    ),
                    DateListUiState(
                        title = "Yearly",
                        value = "Yearly",
                        isOpenDialog = false,
                        enable = true,
                    ),
                ).toImmutableList(),
            )
        }
        setDatePicker(
            currentDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
        )
        setTimePicker(
            hour = currentDateTime.hour,
            minute = currentDateTime.minute,
        )
    }

    fun onSetDate(index: Int) {
        if (index == dateTimeState.value.dateData.lastIndex) {
            _dateTimeState.update {
                it.copy(
                    showDateDialog = true,
                )
            }
        } else {
            val date2 = if (index == 0) today.date else today.date.plus(1, DateTimeUnit.DAY)
            val time = timeListDefault[dateTimeState.value.currentTime]
            val localtimedate = LocalDateTime(date2, time)
            _dateTimeState.update {
                it.copy(
                    currentDate = index,
                    timeError = today > localtimedate,
                )
            }
            val date = if (index == 0) {
                System.currentTimeMillis()
            } else {
                System.currentTimeMillis() + 24 * 60 * 60 * 1000
            }
            setDatePicker(date)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun setDatePicker(date: Long) {
        datePicker = DatePickerState(
            initialSelectedDateMillis = date,
            locale = Locale.getDefault(),
        )
    }

    fun onSetTime(index: Int) {
        if (index == dateTimeState.value.timeData.lastIndex) {
            _dateTimeState.update {
                it.copy(
                    showTimeDialog = true,
                )
            }
        } else {
            _dateTimeState.update {
                it.copy(
                    currentTime = index,
                    timeError = false,
                )
            }
            setTimePicker(
                timeListDefault[index].hour,
                timeListDefault[index].minute,
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun setTimePicker(hour: Int, minute: Int) {
        timePicker = TimePickerState(hour, minute, false)
    }

    fun onSetInterval(index: Int) {
        _dateTimeState.update {
            it.copy(currentInterval = index)
        }
    }

    fun setAlarm() {
        val time = timeListDefault[dateTimeState.value.currentTime]
        val date = when (dateTimeState.value.currentDate) {
            0 -> today.date
            1 -> today.date.plus(1, DateTimeUnit.DAY)
            else -> currentLocalDate
        }
        val interval = when (dateTimeState.value.currentInterval) {
            0 -> null
            1 -> DateTimeUnit.HOUR.times(24).duration.toLong(DurationUnit.MILLISECONDS)

            2 -> DateTimeUnit.HOUR.times(24 * 7).duration.toLong(DurationUnit.MILLISECONDS)

            3 -> DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)

            else -> DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)
        }

        val setime = LocalDateTime(date, time)
        if (setime > today) {
            setAlarm(
                setime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                interval,
            )
            Log.e("editv", "Set Alarm")
        } else {
            Log.e("editv", "Alarm not set $today time $time date$date")
        }
    }

    fun hideTime() {
        _dateTimeState.update {
            it.copy(showTimeDialog = false)
        }
    }

    fun hideDate() {
        _dateTimeState.update {
            it.copy(showDateDialog = false)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onSetDate() {
        datePicker.selectedDateMillis?.let { timee ->
            val date = Instant.fromEpochMilliseconds(timee)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            currentLocalDate = date.date
            val time = timeListDefault[dateTimeState.value.currentTime]
            val localtimedate = LocalDateTime(currentLocalDate, time)

            _dateTimeState.update {
                val im = it.dateData.toMutableList()
                im[im.lastIndex] = im[im.lastIndex].copy(value = notepadRepository.dateToString(date.date))
                it.copy(
                    dateData = im.toImmutableList(),
                    currentDate = im.lastIndex,
                    timeError = today > localtimedate,
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onSetTime() {
        val time = LocalTime(timePicker.hour, timePicker.minute)

        timeListDefault[timeListDefault.lastIndex] = time
        val date = when (dateTimeState.value.currentDate) {
            0 -> today.date
            1 -> today.date.plus(1, DateTimeUnit.DAY)
            else -> currentLocalDate
        }
        val datetime = LocalDateTime(date, time)

        Log.e("onSettime", "current $today date $datetime")

        _dateTimeState.update {
            val im = it.timeData.toMutableList()
            im[im.lastIndex] = im[im.lastIndex].copy(value = notepadRepository.timeToString(time))
            it.copy(
                timeData = im.toImmutableList(),
                currentTime = im.lastIndex,
                timeError = datetime < today,
            )
        }
    }

    fun getSuccess() = mainState.value as MainState.Success
}
