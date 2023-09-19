package com.mshdabiola.mainscreen

import android.net.Uri
import android.util.Log
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.AlarmManager
import com.mshdabiola.common.ContentManager
import com.mshdabiola.common.DateShortStringUsercase
import com.mshdabiola.common.DateStringUsercase
import com.mshdabiola.common.Time12UserCase
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NoteLabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.database.repository.NoteRepository
import com.mshdabiola.designsystem.component.state.DateDialogUiData
import com.mshdabiola.designsystem.component.state.DateListUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import com.mshdabiola.designsystem.component.state.Notify
import com.mshdabiola.designsystem.component.state.toLabelUiState
import com.mshdabiola.designsystem.component.state.toNotePad
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
import javax.inject.Inject
import kotlin.time.DurationUnit

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val notepadRepository: NotePadRepository,
    private val contentManager: ContentManager,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val noteRepository: NoteRepository,
    private val alarmManager: AlarmManager,
    private val time12UserCase: Time12UserCase,
    private val dateShortStringUsercase: DateShortStringUsercase,
    private val dateStringUsercase: DateStringUsercase
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
                },
            )
                .distinctUntilChanged { old, new -> old == new }
                .collectLatest { pair ->

                    _mainState.value = mainState.value.copy(
                        labels = pair.first.map { it.toLabelUiState() }.toImmutableList(),
                    )

                    when (pair.second.type) {
                        NoteType.LABEL -> {
                            notepadRepository.getNotePads().map { notes ->
                                notes.filter { it -> it.labels.any { it.labelId == (pair.second).id } }
                                    .map { it.toNotePadUiState(pair.first, getTime = dateShortStringUsercase::invoke, toPath = contentManager::getImagePath) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList(),
                                    )
                                }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }

                        NoteType.REMAINDER -> {
                            notepadRepository.getNotePads().map { notes ->
                                notes.map { it.toNotePadUiState(pair.first,getTime = dateShortStringUsercase::invoke, toPath = contentManager::getImagePath) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.filter { it.note.reminder > 0 }.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList(),
                                    )
                                }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }

                        else -> {
                            notepadRepository.getNotePads(pair.second.type).map { notes ->
                                notes.map { it.toNotePadUiState(pair.first, getTime = dateShortStringUsercase::invoke, toPath = contentManager::getImagePath) }
                            }.collect { padUiStateList ->
                                val list = padUiStateList.map {
                                    val labels = it.labels
                                        .take(3)
                                        .mapIndexed { index, s -> if (index == 2) "${it.labels.size - 2}+" else s }
                                    it.copy(
                                        images = it.images.takeLast(6).toImmutableList(),
                                        labels = labels.toImmutableList(),
                                    )
                                }
                                    .filter { !it.isEmpty() }
                                _mainState.value =
                                    mainState.value.copy(notePads = list.toImmutableList())
                            }
                        }
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            initDate()
        }
    }

    fun onSelectCard(id: Long) {
        val listNOtePad = mainState.value.notePads.toMutableList()
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

    fun setNoteType(noteType: NoteTypeUi) {
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

    private fun setAlarm(time: Long, interval: Long?) {
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
                    noteId = it.id ?: 0L,
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
        val notes = selectedNotes.map { it.copy(noteType = NoteType.ARCHIVE) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }
    }

    fun setAllDelete() {
        val selectedNotes = mainState.value.notePads
            .filter { it.note.selected }
            .map { it.toNotePad().note }

        clearSelected()
        val notes = selectedNotes.map { it.copy(noteType = NoteType.TRASH) }

        viewModelScope.launch {
            noteRepository.upsert(notes)
        }
    }

    fun copyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val id = mainState.value.notePads.single { it.note.selected }.note.id
            val notepads = notepadRepository.getOneNotePad(id).first()

            var copy = notepads.copy(note = notepads.note.copy(id = null))

            val newId = notepadRepository.insertNotepad(copy)

            copy = copy.copy(
                note = copy.note.copy(id = newId),
                images = copy.images.map { it.copy(noteId = newId) },
                voices = copy.voices.map { it.copy(noteId = newId) },
                labels = copy.labels.map { it.copy(noteId = newId) },
                checks = copy.checks.map { it.copy(noteId = newId) },
            )

            notepadRepository.insertNotepad(copy)
        }
    }

    fun deleteLabel() {
        val labelId = (mainState.value.noteType).id

        _mainState.value = mainState.value.copy(noteType = NoteTypeUi())

        viewModelScope.launch {
            labelRepository.delete(labelId)
            noteLabelRepository.deleteByLabelId(labelId)
        }
    }

    fun renameLabel(name: String) {
        val labelId = (mainState.value.noteType).id

        viewModelScope.launch {
            labelRepository.upsert(listOf(Label(labelId, name)))
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            notepadRepository.deleteTrashType()
        }
    }



    fun deleteEmptyNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val emptyList = notepadRepository
                .getNotePads()
                .first()
                .map { it.toNotePadUiState(getTime = dateShortStringUsercase::invoke, toPath = contentManager::getImagePath) }
                .filter { it.isEmpty() }

            if (emptyList.isNotEmpty()) {
                notepadRepository.deleteNotePad(emptyList.map { it.toNotePad() })

                addNotify("Remove empty note")
            }
        }
    }

    fun onToggleGrid() {
        val grid = mainState.value.isGrid

        _mainState.value = mainState.value.copy(isGrid = !grid)
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
        LocalTime(20, 0, 0)
    )


    @OptIn(ExperimentalMaterial3Api::class)
    var datePicker: DatePickerState = DatePickerState(
        System.currentTimeMillis(), System.currentTimeMillis(),
        DatePickerDefaults.YearRange,
        DisplayMode.Picker
    )
    @OptIn(ExperimentalMaterial3Api::class)
    var timePicker: TimePickerState = TimePickerState(12, 4, is24Hour = false)
    private lateinit var currentLocalDate :LocalDate

    //date and time dialog logic

    private fun initDate() {
        val now = Clock.System.now()
        today = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val today2=now.plus(10, DateTimeUnit.MINUTE).toLocalDateTime(TimeZone.currentSystemDefault())
        currentDateTime = today2
        currentLocalDate=currentDateTime.date
        Log.e("current date",currentLocalDate.toString())


        val timeList = mutableListOf(
            DateListUiState(
                title = "Morning",
                value = "7:00 AM",
                trail = "7:00 AM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Afternoon",
                value = "1:00 PM",
                trail = "1:00 PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Evening",
                value = "7:00 PM",
                trail = "7:00 PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Night",
                value = "8:00 PM",
                trail = "8:00 PM",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Pick time",
                value = "1:00 PM",
                isOpenDialog = true,
                enable = true
            )

        )
            .mapIndexed { index, dateListUiState ->
                if (index != timeListDefault.lastIndex) {

                    val greater = timeListDefault[index] > today.time
                    dateListUiState.copy(
                        enable = greater,
                        value = time12UserCase(timeListDefault[index]),
                        trail = time12UserCase(timeListDefault[index])
                    )
                } else {
                    timeListDefault[timeListDefault.lastIndex]=currentDateTime.time
                    dateListUiState.copy( value = time12UserCase(currentDateTime.time))

                }
            }
            .toImmutableList()
        val datelist=listOf(
            DateListUiState(
                title = "Today",
                value = "Today",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Tomorrow",
                value = "Tomorrow",
                isOpenDialog = false,
                enable = true
            ),
            DateListUiState(
                title = "Pick date",
                value = dateStringUsercase(currentDateTime.date),
                isOpenDialog = true,
                enable = true
            )
        ).toImmutableList()
        val interval = 0




        _dateTimeState.update {
            it.copy(
                isEdit = false,
                currentTime = timeList.lastIndex ,
                timeData = timeList,
                timeError = today>currentDateTime,
                currentDate =  0,
                dateData = datelist,
                currentInterval = interval,
                interval = listOf(
                    DateListUiState(
                        title = "Does not repeat",
                        value = "Does not repeat",
                        isOpenDialog = false,
                        enable = true
                    ),
                    DateListUiState(
                        title = "Daily",
                        value = "Daily",
                        isOpenDialog = false,
                        enable = true
                    ),
                    DateListUiState(
                        title = "Weekly",
                        value = "Weekly",
                        isOpenDialog = false,
                        enable = true
                    ),
                    DateListUiState(
                        title = "Monthly",
                        value = "Monthly",
                        isOpenDialog = false,
                        enable = true
                    ),
                    DateListUiState(
                        title = "Yearly",
                        value = "Yearly",
                        isOpenDialog = false,
                        enable = true
                    )
                ).toImmutableList()
            )
        }
        setDatePicker(
            currentDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        )
        setTimePicker(
            hour = currentDateTime.hour,
            minute = currentDateTime.minute
        )
    }

    fun onSetDate(index: Int) {
        if (index == dateTimeState.value.dateData.lastIndex) {
            _dateTimeState.update {
                it.copy(
                    showDateDialog = true
                )
            }
        } else {
            val date2=if (index==0)today.date else today.date.plus(1,DateTimeUnit.DAY)
            val time=timeListDefault[dateTimeState.value.currentTime]
            val localtimedate=LocalDateTime(date2,time)
            _dateTimeState.update {

                it.copy(
                    currentDate = index,
                    timeError = today>localtimedate
                )
            }
            val date = if (index == 0)
                System.currentTimeMillis()
            else
                System.currentTimeMillis() + 24 * 60 * 60 * 1000
            setDatePicker(date)
        }

    }



    @OptIn(ExperimentalMaterial3Api::class)
    fun setDatePicker(date: Long) {
        datePicker = DatePickerState(
            date, date,
            DatePickerDefaults.YearRange,
            DisplayMode.Picker
        )
    }

    fun onSetTime(index: Int) {
        if (index == dateTimeState.value.timeData.lastIndex) {
            _dateTimeState.update {
                it.copy(
                    showTimeDialog = true
                )
            }
        } else {
            _dateTimeState.update {
                it.copy(
                    currentTime = index,
                    timeError = false
                )
            }
            setTimePicker(
                timeListDefault[index].hour,
                timeListDefault[index].minute
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
            setAlarm(setime
                .toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds(), interval)
            Log.e("editv","Set Alarm")
        }else{
            Log.e("editv","Alarm not set $today time $time date$date")
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
            val time=timeListDefault[dateTimeState.value.currentTime]
            val localtimedate=LocalDateTime(currentLocalDate,time)

            _dateTimeState.update {
                val im = it.dateData.toMutableList()
                im[im.lastIndex] =
                    im[im.lastIndex].copy(value = dateStringUsercase(date.date))
                it.copy(
                    dateData = im.toImmutableList(),
                    currentDate = im.lastIndex,
                    timeError = today>localtimedate
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
        val datetime=LocalDateTime(date,time)

        Log.e("onSettime","current $today date $datetime")


        _dateTimeState.update {
            val im = it.timeData.toMutableList()
            im[im.lastIndex] = im[im.lastIndex].copy(value = time12UserCase(time))
            it.copy(
                timeData = im.toImmutableList(),
                currentTime = im.lastIndex,
                timeError = datetime<today
            )
        }
    }

    private fun addNotify(text: String) {
        val notifies = mainState.value.messages.toMutableList()

        notifies.add(Notify(message = text, callback = ::onNotifyDelive))
        _mainState.update {
            it.copy(messages = notifies.toImmutableList())
        }
    }

    private fun onNotifyDelive() {
        Timber.d("Remove")
        val notifies = mainState.value.messages.toMutableList()

        notifies.removeFirst()
        _mainState.value = mainState.value.copy(messages = notifies.toImmutableList())
    }


}
