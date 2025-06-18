/*
 *abiola 2022
 */

package com.mshdabiola.detail

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.common.INotePlayer
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationPlace
import com.mshdabiola.model.NotificationUiState
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notePadRepository: INotePadRepository,
    private val alarmManager: IAlarmManager,
    private val voicePlayer: INotePlayer,

) : ViewModel() {

    val notificationUiState = NotificationUiState(
        currentDateTime = LocalDateTime(2026, 6, 16, 22, 1),
        currentInterval = NotificationInterval.Daily(
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = NotificationPlace.Home,

    )
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
                    if (note.value.id != -1L) {

                        note.update { it.copy(title = text.toString()) }
                        saveNote()
                    }
                }
        }
        viewModelScope.launch {
            snapshotFlow { content.text }
                .debounce(500)
                .collectLatest { text ->
                    if (note.value.id != -1L) {
                        note.update { it.copy(detail = text.toString()) }
                        saveNote()
                    }
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

    fun setAlarm() {
//        val time = timeList[dateTimeState.value.currentTime]
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
//        val now = today.toInstant(TimeZone.currentSystemDefault())
//        val setime = LocalDateTime(date, time).toInstant(TimeZone.currentSystemDefault())
//        if (setime.toEpochMilliseconds() > now.toEpochMilliseconds()) {
//            setAlarm(setime.toEpochMilliseconds(), interval)
//            // Timber.tag("editv").e("Set Alarm")
// //            addNotify("Alarm is set")
//        } else {
//            // Timber.tag("editv").e("Alarm not set " + now + " " + setime)
// //            addNotify("Alarm not set, time as past")
//        }
    }

    fun deleteAlarm() {
//        val note2 = note.value.copy(reminder = -1, interval = -1)
//        note.update {
//            note2
//        }
//
//        viewModelScope.launch {
//            alarmManager.deleteAlarm(note2.id.toInt())
//        }
    }

    fun setAlarm(time: Long, interval: Long?) {
//        val noteN = note.value.copy(
//            reminder = time,
//            interval = interval ?: -1,
//            reminderString = notePadRepository.dateToString(time),
//        )
//        note.update {
//            noteN
//        }
//
//        viewModelScope.launch {
//            alarmManager.setAlarm(
//                time,
//                interval,
//                requestCode = noteN.id.toInt(),
//                title = noteN.title,
//                content = noteN.detail,
//                noteId = noteN.id,
//            )
//        }
    }

    fun saveImage(uri: String) {
        val id = notePadRepository.saveImage(uri)

        val image = NoteImage(
            id = id,
            path = notePadRepository.getImagePath(id),
        )

        note.update {
            it.copy(images = it.images + image)
        }
    }

    fun saveVoice(uri: String, text: String) {
        val id = notePadRepository.saveVoice(uri)

        val voice = NoteVoice(
            id = id,
            voiceName = notePadRepository.getVoicePath(id),
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

    fun insertNewDrawing(): Long {
        val id = System.currentTimeMillis()
        val drawing = NoteImage(
            id = id,
            isDrawing = true,
            path = notePadRepository.getImagePath(id),
        )

        note.update {
            it.copy(images = it.images + drawing)
        }

        return id
    }

    private var playJob: Job? = null
    private var currentIndex = -1
    fun playMusic(index: Int) {
        playJob?.cancel()
        var voices = note.value.voices.toMutableList()

        val voiceUiState = voices[index]

        if (currentIndex != index) {
            voices = voices.map { it.copy(currentProgress = 0, isPlaying = false) }.toMutableList()
            note.update { it.copy(voices = voices) }
        }
        currentIndex = index
        playJob = viewModelScope.launch {
            voicePlayer.playMusic(voiceUiState.voiceName, voiceUiState.currentProgress.toInt())
                .collectLatest { currentProgress ->

                    voices = note.value.voices.toMutableList()

                    voices[index] =
                        note.value.voices[index].copy(currentProgress = currentProgress.toLong(), isPlaying = true)

                    note.update { it.copy(voices = voices) }
                }
            voices = note.value.voices.toMutableList()
            voices[index] = voiceUiState.copy(currentProgress = 0, isPlaying = false)
            note.update { it.copy(voices = voices) }
        }
    }

    fun pause() {
        // prevIndex=currentIndex
        var voices = note.value.voices.toMutableList()

        val voiceUiState = voices[currentIndex]
        voices[currentIndex] = voiceUiState.copy(isPlaying = false)
        note.update { it.copy(voices = voices) }
        playJob?.cancel()
        voicePlayer.pause()
    }
}
