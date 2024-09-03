/*
 *abiola 2022
 */

package com.mshdabiola.detail

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.ui.state.DateDialogUiData
import com.mshdabiola.ui.state.DateListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val alarmManager: IAlarmManager,

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
        println("save note ${note.value}")
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
            // notePadUiState = notePadUiState.copy(uris = uri)
        }
    }

    fun onCheckChange(text: String, id: Long) {
        val noteChecks = note.value.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(content = text)
        noteChecks[index] = noteCheck
        note.update {
            it.copy(checks = noteChecks)
        }
    }

    fun addCheck() {
        viewModelScope.launch {
            val noteCheck = NoteCheck(isCheck = false)
            val noteChecks = note.value.checks.toMutableList()
            noteChecks.add(noteCheck)
            notePadRepository.upsert(note.value.copy(checks = noteChecks))
            val noteWithCheckId = notePadRepository.getOneNotePad(note.value.id)
                .first()!!
            note.update {
                noteWithCheckId
            }
        }
    }

    fun onCheck(check: Boolean, id: Long) {
        val noteChecks = note.value.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(isCheck = check)
        noteChecks[index] = noteCheck
        println(noteCheck)
        note.update {
            it.copy(checks = noteChecks)
        }
    }

    fun onCheckDelete(id: Long) {
        val noteChecks = note.value.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks.removeAt(index)
        note.update {
            it.copy(checks = noteChecks)
        }
        viewModelScope.launch {
            notePadRepository.deleteCheckNote(id, noteCheck.noteId)
        }
    }

    fun changeToCheckBoxes() {
        viewModelScope.launch {
            val newNote = content.text.split("\n")
            val noteChecks = newNote.map { s ->
                NoteCheck(content = s, isCheck = false)
            }
            notePadRepository.upsert(
                note.value.copy(
                    detail = "",
                    checks = noteChecks,
                    isCheck = true,
                ),
            )
            val noteN = notePadRepository.getOneNotePad(id).first()!!
            note.update {
                noteN
            }
            content.clearText()
        }
    }

    fun unCheckAllItems() {
        val noteChecks = note.value.checks.map { it.copy(isCheck = false) }
        note.update {
            it.copy(checks = noteChecks)
        }
    }

    fun deleteCheckedItems() {
        val checkNote = note.value.checks.filter { it.isCheck }
        val notCheckNote = note.value.checks.filter { !it.isCheck }

        note.update {
            it.copy(checks = notCheckNote)
        }
        viewModelScope.launch {
            checkNote.forEach {
                notePadRepository.deleteCheckNote(it.id, it.noteId)
            }
        }
    }

    fun hideCheckBoxes() {
        val noteCheck = note.value.checks.joinToString(separator = "\n") { it.content }

        note.update {
            it.copy(detail = noteCheck, isCheck = false, checks = emptyList())
        }
        content.edit {
            append(noteCheck)
        }

        viewModelScope.launch {
            notePadRepository.deleteNoteCheckByNoteId(note.value.id)
        }
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

    fun pinNote() {
        note.update {
            it.copy(isPin = !it.isPin)
        }
    }

    fun onColorChange(index: Int) {
        note.update {
            it.copy(color = index)
        }
    }

    fun onImageChange(index: Int) {
        note.update {
            it.copy(background = index)
        }
    }

    fun setAlarm(time: Long, interval: Long?) {
        val noteN = note.value.copy(
            reminder = time,
            interval = interval ?: -1,
            reminderString = notePadRepository.dateToString(time),
        )
        note.update {
            noteN
        }

        viewModelScope.launch {
            alarmManager.setAlarm(
                time,
                interval,
                requestCode = noteN.id.toInt(),
                title = noteN.title,
                content = noteN.detail,
                noteId = noteN.id,
            )
        }
    }

    fun deleteAlarm() {
        val note2 = note.value.copy(reminder = -1, interval = -1)
        note.update {
            note2
        }

        viewModelScope.launch {
            alarmManager.deleteAlarm(note2.id.toInt())
        }
    }

    fun onArchive() {
        var note2 = note.value
        note2 = if (note2.noteType == NoteType.ARCHIVE) {
            note2.copy(noteType = NoteType.NOTE)
        } else {
            note2.copy(noteType = NoteType.ARCHIVE)
        }
        note.update {
            note2
        }
    }

    fun onDelete() {
//
        note.update {
            it.copy(noteType = NoteType.TRASH)
        }
    }

    fun copyNote() {
        viewModelScope.launch {
            var note2 = note.value

            note2 = note2.copy(
                id = -1,
                checks = note2.checks.map { it.copy(id = -1) },
                images = note2.images.map { it.copy(id = -1) },
                voices = note2.voices.map { it.copy(id = -1) },
            )

            notePadRepository.upsert(note2)
        }
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
        viewModelScope.launch {
            val voices = note.value.voices.toMutableList()
            val voice = voices.removeAt(index)

            notePadRepository.deleteVoiceNote(voice.id)
            note.update {
                it.copy(voices = voices)
            }
        }
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
                        value = notePadRepository.timeToString(timeList[index]),
                        trail = notePadRepository.timeToString(timeList[index]),
                    )
                } else {
                    dateListUiState.copy(value = notePadRepository.timeToString(currentDateTime.time))
                }
            }
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
                value = notePadRepository.dateToString(currentDateTime.date),
                isOpenDialog = true,
                enable = true,
            ),
        )
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
                ),
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
                    im[im.lastIndex].copy(value = notePadRepository.dateToString(date.date))
                it.copy(
                    dateData = im,
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
            im[im.lastIndex] = im[im.lastIndex].copy(value = notePadRepository.timeToString(time))
            it.copy(
                timeData = im,
                currentTime = im.lastIndex,
                timeError = datetime < today,
            )
        }
    }

    fun saveImage(uri: String) {
        val id = notePadRepository.saveImage(uri)

        val image = NoteImage(
            id = id,
        )

        note.update {
            it.copy(images = it.images + image)
        }
    }

    fun saveVoice(uri: String, text: String) {
        val id = notePadRepository.saveVoice(uri)

        val voice = NoteVoice(
            id = id,
        )
        content.edit {
            append(text)
        }

        note.update {
            it.copy(voices = it.voices + voice)
        }
    }

    fun getPhotoUri(): String {
        return notePadRepository.getUri()
    }
}
