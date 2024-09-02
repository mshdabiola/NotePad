/*
 *abiola 2022
 */

package com.mshdabiola.detail

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.DateStringUsercase
import com.mshdabiola.common.IContentManager
import com.mshdabiola.common.INotePlayer
import com.mshdabiola.common.TimeUsercase
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteUri
import com.mshdabiola.ui.state.DateDialogUiData
import com.mshdabiola.ui.state.DateListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

@OptIn(FlowPreview::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notePadRepository: INotePadRepository,
    private val contentManager: IContentManager,
    private val voicePlayer: INotePlayer,
    private val labelRepository: ILabelRepository,
    private val time12UserCase: TimeUsercase,
    private val dateStringUsercase: DateStringUsercase,

) : ViewModel() {

    private val id = savedStateHandle.toRoute<DetailArg>().id
    val note = MutableStateFlow(NotePad())

    val title = TextFieldState()
    val content = TextFieldState()

    private val _state = MutableStateFlow<DetailState>(DetailState.Loading())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val initNOte = notePadRepository.getOneNotePad(id)
                .first()!!
            note.update { initNOte }

            _state.update { DetailState.Success(id) }

            title.edit {
                append(initNOte.title)
            }
            content.edit {
                append(initNOte.detail)
            }

            note
                .collectLatest {
                    saveNote()
                }
        }

        viewModelScope.launch {
            snapshotFlow { title.text }
                .debounce(500)
                .collectLatest { text ->
                    note.update { it.copy(title = text.toString()) }
                    saveNote()
                }
        }
        viewModelScope.launch {
            snapshotFlow { content.text }
                .debounce(500)
                .collectLatest { text ->
                    note.update { it.copy(detail = text.toString()) }
                    saveNote()
                }
        }
    }

    private suspend fun saveNote() {
        println("save note")
        notePadRepository.upsert(note.value)
    }

    private suspend fun computeUri(notepad: NotePad) = withContext(Dispatchers.IO) {
        val regex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

        if (notepad.detail.contains(regex.toRegex())) {
            val uri = notepad.detail.split("\\s".toRegex())
                .filter { it.trim().matches(regex.toRegex()) }
                .mapIndexed { index, s ->
                    val path = s.toUri().authority ?: ""
                    val icon = "https://icon.horse/icon/$path"
                    NoteUri(
                        id = index,
                        icon = icon,
                        path = path,
                        uri = s,
                    )
                }
                .toImmutableList()
            // notePadUiState = notePadUiState.copy(uris = uri)
        }
    }

    fun onCheckChange(text: String, id: Long) {
        val noteChecks = note.value.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(content = text)
        noteChecks[index] = noteCheck
        note.update {
            it.copy(checks = noteChecks.toImmutableList())
        }
    }

    fun addCheck() {
        val noteCheck = NoteCheck()
    }

    fun onCheck(check: Boolean, id: Long) {
//        val noteChecks = notePadUiState.checks.toMutableList()
//        val index = noteChecks.indexOfFirst { it.id == id }
//        val noteCheck = noteChecks[index].copy(isCheck = check)
//        noteChecks[index] = noteCheck
//        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheckDelete(id: Long) {
//        val noteChecks = notePadUiState.checks.toMutableList()
//        val index = noteChecks.indexOfFirst { it.id == id }
//        val noteCheck = noteChecks.removeAt(index)
//        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
//        viewModelScope.launch {
//            notePadRepository.deleteCheckNote(id, noteCheck.noteId)
//        }
    }

    private var playJob: Job? = null
    private var currentIndex = -1
    fun playMusic(index: Int) {
//        playJob?.cancel()
//        val voiceUiState = notePadUiState.voices[index]
//        var voices = notePadUiState.voices.toMutableList()
//
//        if (currentIndex != index) {
//            voices = voices.map { it.copy(currentProgress = 0f, isPlaying = false) }.toMutableList()
//            notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
//        }
//        currentIndex = index
//        playJob = viewModelScope.launch {
//            voicePlayer.playMusic(voiceUiState.voiceName, voiceUiState.currentProgress.toInt())
//                .collect {
//                    voices[index] =
//                        voiceUiState.copy(currentProgress = it.toFloat(), isPlaying = true)
//
//                    notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
//                }
//            voices[index] = voiceUiState.copy(currentProgress = 0f, isPlaying = false)
//            notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
//        }
    }

    fun pause() {
        // prevIndex=currentIndex
//        val voiceUiState = notePadUiState.voices[currentIndex]
//        val voices = notePadUiState.voices.toMutableList()
//        voices[currentIndex] = voiceUiState.copy(isPlaying = false)
//        notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
//        playJob?.cancel()
//        voicePlayer.pause()
    }

    fun changeToCheckBoxes() {
//        val newNote = notePadUiState.note.detail.split("\n")
//        val id = notePadUiState.note.id
//        val noteChecks = newNote.map { s ->
//            NoteCheck(id = getNewId(), noteId = id, content = s).toNoteCheckUiState()
//        }
//        notePadUiState = notePadUiState.copy(
//            note = notePadUiState.note.copy(isCheck = true, detail = ""),
//            checks = noteChecks.toImmutableList(),
//        )
    }

    fun unCheckAllItems() {
//        val noteChecks = notePadUiState.checks.map { it.copy(isCheck = false) }
//
//        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun deleteCheckedItems() {
//        val checkNote = notePadUiState.checks.filter { it.isCheck }
//        val notCheckNote = notePadUiState.checks.filter { !it.isCheck }
//
//        notePadUiState = notePadUiState.copy(checks = notCheckNote.toImmutableList())
//
//        viewModelScope.launch {
//            checkNote.forEach {
//                notePadRepository.deleteCheckNote(it.id, it.noteId)
//            }
//        }
    }

    fun hideCheckBoxes() {
//        val noteCheck = notePadUiState.checks.joinToString(separator = "\n") { it.content }
//
//        notePadUiState = notePadUiState.copy(
//            note = notePadUiState.note.copy(detail = noteCheck, isCheck = false),
//            checks = emptyList<NoteCheckUiState>().toImmutableList(),
//        )
//
//        viewModelScope.launch {
//            notePadRepository.deleteNoteCheckByNoteId(notePadUiState.note.id)
//        }
    }

    fun pinNote() {
//        notePadUiState =
//            notePadUiState.copy(note = notePadUiState.note.copy(isPin = !notePadUiState.note.isPin))
//
//        if (notePadUiState.note.isPin) {
//            addNotify("Note is pinned")
//        } else {
//            addNotify("Note is not pinned")
//        }
    }

    fun onColorChange(index: Int) {
//        val note = notePadUiState.note.copy(color = index)
//        notePadUiState = notePadUiState.copy(note = note)
    }

    fun onImageChange(index: Int) {
//        val note = notePadUiState.note.copy(background = index)
//        notePadUiState = notePadUiState.copy(note = note)
    }

    fun setAlarm(time: Long, interval: Long?) {
//        val note = notePadUiState.note.copy(
//            reminder = time,
//            interval = interval ?: -1,
//            date = dateShortStringUsercase(time),
//        )
//        notePadUiState = notePadUiState.copy(note = note)
//
//        viewModelScope.launch {
//            alarmManager.setAlarm(
//                time,
//                interval,
//                requestCode = notePadUiState.note.id.toInt(),
//                title = notePadUiState.note.title,
//                content = notePadUiState.note.detail,
//                noteId = notePadUiState.note.id,
//            )
//        }
    }

    fun deleteAlarm() {
//        val note = notePadUiState.note.copy(reminder = -1, interval = -1)
//        notePadUiState = notePadUiState.copy(note = note)
//
//        viewModelScope.launch {
//            val id = note.id
//
//            alarmManager.deleteAlarm(id.toInt())
//            addNotify("Alarm deleted")
//        }
    }

    fun onArchive() {
//        notePadUiState = if (notePadUiState.note.noteType.type == NoteType.ARCHIVE) {
//            val note = notePadUiState.note.copy(noteType = NoteTypeUi())
//            addNotify("Note archived")
//            notePadUiState.copy(note = note)
//        } else {
//            val note = notePadUiState.note.copy(noteType = NoteTypeUi(NoteType.ARCHIVE))
//            addNotify("Note already archived")
//            notePadUiState.copy(note = note)
//        }
    }

    fun onDelete() {
//        val note = notePadUiState.note.copy(noteType = NoteTypeUi(NoteType.TRASH))
//        notePadUiState = notePadUiState.copy(note = note)
    }

    fun copyNote() {
//        viewModelScope.launch {
//            val notepads = notePadUiState.toNotePad()
//
//            var copy = notepads.copy(note = notepads.note.copy(id = null))
//
//            val newId = notePadRepository.insertNotepad(copy)
//
//            copy = copy.copy(
//                note = copy.note.copy(id = newId),
//                images = copy.images.map { it.copy(noteId = newId) },
//                voices = copy.voices.map { it.copy(noteId = newId) },
//                labels = copy.labels.map { it.copy(noteId = newId) },
//                checks = copy.checks.map { it.copy(noteId = newId) },
//            )
//
//            notePadRepository.insertNotepad(copy)
//            addNotify("Note copied")
//        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getAudioLength(path: String): Long {
        val mediaMetadataRetriever = MediaMetadataRetriever()

        mediaMetadataRetriever.setDataSource(path)
        val time =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        // Log.e(this::class.simpleName, "$time time")
        return time?.toLong() ?: 1L
    }

    fun deleteVoiceNote(index: Int) {
//        viewModelScope.launch {
//            val voices = notePadUiState.voices.toMutableList()
//            val voice = voices.removeAt(index)
//            notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
//
//            noteVoiceRepository.delete(voice.id)
//
//            addNotify("Voice note deleted")
//        }
    }

    private fun onImage(path: String, notePad: NotePad) {
//        viewModelScope.launch {
//            try {
//                // val image = notePad.images[index]
//                val text = try {
//                    imageToText.toText(path)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    ""
//                }
//                val note = notePad.note
//                notePadUiState =
//                    notePadUiState.copy(note = note.copy(detail = "${note.detail}\n$text"))
//                addNotify("Image text extracted")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                addNotify("Error occur during extract of image")
//            }
//        }
    }

    private val _dateTimeState = MutableStateFlow(DateDialogUiData())
    val dateTimeState = _dateTimeState.asStateFlow()
    private lateinit var currentDateTime: LocalDateTime
    private lateinit var today: LocalDateTime
    private val timeList = mutableListOf(
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
    private var currentLocalDate = LocalDate(1, 2, 3)

    // date and time dialog logic

    private fun initDate(note: NotePad) {
        val now = Clock.System.now()
        today = now.toLocalDateTime(TimeZone.currentSystemDefault())
        currentDateTime =
            if (note.reminder > 0) {
                Instant.fromEpochMilliseconds(note.reminder).toLocalDateTime(
                    TimeZone.currentSystemDefault(),
                )
            } else {
                today
            }
        currentLocalDate = currentDateTime.date

        val timeList = listOf(
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

        )
            .mapIndexed { index, dateListUiState ->
                if (index != timeList.lastIndex) {
                    val greater = timeList[index] > today.time
                    dateListUiState.copy(
                        enable = greater,
                        value = time12UserCase(timeList[index]),
                        trail = time12UserCase(timeList[index]),
                    )
                } else {
                    dateListUiState.copy(value = time12UserCase(currentDateTime.time))
                }
            }
            .toImmutableList()
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
                value = dateStringUsercase(currentDateTime.date),
                isOpenDialog = true,
                enable = true,
            ),
        ).toImmutableList()
        val interval = when (note.interval) {
            DateTimeUnit.HOUR.times(24).duration.toLong(DurationUnit.MILLISECONDS) -> 1

            DateTimeUnit.HOUR.times(24 * 7).duration.toLong(DurationUnit.MILLISECONDS) -> 2

            DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS) -> 3

            DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS) -> 4
            else -> 0
        }

        _dateTimeState.update {
            it.copy(
                isEdit = note.reminder > 0,
                currentTime = if (note.reminder > 0) timeList.lastIndex else 0,
                timeData = timeList,
                timeError = today > currentDateTime,
                currentDate = if (note.reminder > 0) datelist.lastIndex else 0,
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
            val time = timeList[dateTimeState.value.currentTime]
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
                timeList[index].hour,
                timeList[index].minute,
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
        val time = timeList[dateTimeState.value.currentTime]
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
        val now = today.toInstant(TimeZone.currentSystemDefault())
        val setime = LocalDateTime(date, time).toInstant(TimeZone.currentSystemDefault())
        if (setime.toEpochMilliseconds() > now.toEpochMilliseconds()) {
            setAlarm(setime.toEpochMilliseconds(), interval)
            // Timber.tag("editv").e("Set Alarm")
//            addNotify("Alarm is set")
        } else {
            // Timber.tag("editv").e("Alarm not set " + now + " " + setime)
//            addNotify("Alarm not set, time as past")
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
            val time = timeList[dateTimeState.value.currentTime]
            val localtimedate = LocalDateTime(currentLocalDate, time)

            _dateTimeState.update {
                val im = it.dateData.toMutableList()
                im[im.lastIndex] =
                    im[im.lastIndex].copy(value = dateStringUsercase(date.date))
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

        timeList[timeList.lastIndex] = time
        val date = when (dateTimeState.value.currentDate) {
            0 -> today.date
            1 -> today.date.plus(1, DateTimeUnit.DAY)
            else -> currentLocalDate
        }
        val datetime = LocalDateTime(date, time)

        Timber.tag("onSettime").e("current " + today + " date " + datetime)

        _dateTimeState.update {
            val im = it.timeData.toMutableList()
            im[im.lastIndex] = im[im.lastIndex].copy(value = time12UserCase(time))
            it.copy(
                timeData = im.toImmutableList(),
                currentTime = im.lastIndex,
                timeError = datetime < today,
            )
        }
    }
}
