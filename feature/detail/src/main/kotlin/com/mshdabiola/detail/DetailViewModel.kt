/*
 *abiola 2022
 */

package com.mshdabiola.detail

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
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
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVisual
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationPlace
import com.mshdabiola.model.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notePadRepository: INotePadRepository,
    private val alarmManager: IAlarmManager,
    private val voicePlayer: INotePlayer,

) : ViewModel() {

    val detailArg = savedStateHandle.toRoute<DetailArg>()
    val notificationUiState = NotificationUiState(
        currentDateTime = LocalDateTime(2026, 6, 16, 22, 1),
        currentInterval = NotificationInterval.Daily(
            intervalEnd = IntervalEnd.Forever,
        ),
        currentPlace = NotificationPlace.Home,

    )
    private val currentNoteId = MutableStateFlow(detailArg.id)

    private val currentNote = currentNoteId
        .flatMapLatest { ll ->
            notePadRepository
                .getOneNotePad(ll)
        }
    private val initState = DetailState(
        notePad = NotePad(
            id = detailArg.id,
            color = detailArg.colorIndex,
            background = detailArg.background,
        ),
    )

    private var initTitle = false
    val detailState = combine(
        snapshotFlow { initState.title.text }
            .debounce(200),
        snapshotFlow { initState.detail.text }
            .debounce(200),
        currentNote,
    ) { title, content, note ->
        if (note == null) {
            savNewNote()
            initState
        } else {
            if (!initTitle) {
                initState.title.edit {
                    append(note.title)
                }
                initState.detail.edit {
                    append(note.detail)
                }

                initTitle = true
            }
            if (title != note.title || content != note.detail) {
                save(note.copy(title = title.toString(), detail = content.toString()))
            }
            initState.copy(notePad = note)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = initState,
    )

    fun savNewNote() {
        viewModelScope.launch {
            val id = notePadRepository.upsert(NotePad(id = -1))
            currentNoteId.update {
                id
            }
        }
    }

    private fun getNotePad(): NotePad {
        return detailState.value.notePad
    }

    private fun save(notepad: NotePad) {
        viewModelScope.launch {
            notePadRepository.upsert(notepad)
        }
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
        val notepad = getNotePad()
        val noteChecks = notepad.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(content = text)
        noteChecks[index] = noteCheck
        save(notepad.copy(checks = noteChecks))
    }

    fun addCheck() {
        val notepad = getNotePad()
        val noteCheck = NoteCheck(isCheck = false)
        val noteChecks = notepad.checks.toMutableList()
        noteChecks.add(noteCheck)
        save(notepad.copy(checks = noteChecks))
    }

    fun onCheck(check: Boolean, id: Long) {
        val notepad = getNotePad()

        val noteChecks = notepad.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(isCheck = check)
        noteChecks[index] = noteCheck
        println(noteCheck)
        save(notepad.copy(checks = noteChecks))
    }

    fun onCheckDelete(id: Long) {
        val notepad = getNotePad()

        val noteChecks = notepad.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks.removeAt(index)
        viewModelScope.launch {
            notePadRepository.deleteCheckNote(id, noteCheck.noteId)
        }
        save(notepad.copy(checks = noteChecks))
    }

    fun changeToCheckBoxes() {
        viewModelScope.launch {
            val newNote = initState.detail.text.split("\n")

            initState.detail.clearText()
            val noteChecks = newNote.map { s ->
                NoteCheck(content = s, isCheck = false)
            }
            val notepad = getNotePad()

            save(
                notepad.copy(
                    detail = "",
                    checks = noteChecks,
                    isCheck = true,
                ),
            )
        }
    }

    fun unCheckAllItems() {
        val notepad = getNotePad()

        val noteChecks = notepad.checks.map { it.copy(isCheck = false) }
        save(notepad.copy(checks = noteChecks))
    }

    fun deleteCheckedItems() {
        val notepad = getNotePad()

        val checkNote = notepad.checks.filter { it.isCheck }
        val notCheckNote = notepad.checks.filter { !it.isCheck }
        viewModelScope.launch {
            checkNote.forEach {
                notePadRepository.deleteCheckNote(it.id, it.noteId)
            }
        }
        save(notepad.copy(checks = notCheckNote))
    }

    fun hideCheckBoxes() {
        val notepad = getNotePad()

        val noteCheck = notepad.checks.joinToString(separator = "\n") { it.content }

        initState.detail.edit {
            append(noteCheck)
        }

        viewModelScope.launch {
            notePadRepository.deleteNoteCheckByNoteId(notepad.id)
        }
        save(notepad.copy(detail = noteCheck, isCheck = false, checks = emptyList()))
    }

    fun pinNote() {
        val notepad = getNotePad()

        save(notepad.copy(isPin = !notepad.isPin))
    }

    fun onColorChange(index: Int) {
        val notepad = getNotePad()
        save(notepad.copy(color = index))
    }

    fun onImageChange(index: Int) {
        val notepad = getNotePad()
        save(notepad.copy(background = index))
    }

    fun onArchive() {
        val notepad = getNotePad()

        val newNote = if (notepad.noteType == NoteType.ARCHIVE) {
            notepad.copy(noteType = NoteType.NOTE)
        } else {
            notepad.copy(noteType = NoteType.ARCHIVE)
        }

        save(newNote)
    }

    fun onDelete() {
        val notepad = getNotePad()
        save(notepad.copy(noteType = NoteType.TRASH))
    }

    fun copyNote() {
        val note2 = getNotePad()

        val newNotePad = note2.copy(
            id = -1,
            checks = note2.checks.map { it.copy(id = -1) },
            visuals = note2.visuals.map {
                when (it) {
                    is NoteVisual.NoteImage -> it.copy(id = -1)
                    is NoteVisual.NoteDrawing -> it.copy(id = -1)
                }
            },
            voices = note2.voices.map { it.copy(id = -1) },
        )

        save(newNotePad)
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
        val notepad = getNotePad()

        val voices = notepad.voices.toMutableList()
        val voice = voices.removeAt(index)

        viewModelScope.launch {
            notePadRepository.deleteVoiceNote(voice.id)
        }
        save(notepad.copy(voices = voices))
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

        val image = NoteVisual.NoteImage(
            id = id,
            path = notePadRepository.getImagePath(id),
        )

        val notepad = getNotePad()

        save(notepad.copy(visuals = notepad.visuals + image))
    }

    fun saveVoice(uri: String, text: String) {
        val id = notePadRepository.saveVoice(uri)

        val voice = NoteVoice(
            id = id,
            voiceName = notePadRepository.getVoicePath(id),
        )
        initState.detail.edit {
            append(text)
        }

        val notepad = getNotePad()
        save(notepad.copy(voices = notepad.voices + voice))
    }

    fun getPhotoUri(): String {
        return notePadRepository.getUri()
    }

    fun insertNewDrawing(): Long {
        val id = System.currentTimeMillis()
        val drawing = NoteVisual.NoteImage(
            id = id,
            path = notePadRepository.getImagePath(id),
        )
        val notepad = getNotePad()
        save(notepad.copy(visuals = notepad.visuals + drawing))

        return id
    }

    private var playJob: Job? = null
    private var currentIndex = -1
    fun playMusic(index: Int) {
        playJob?.cancel()
        val notepad = getNotePad()

        var voices = notepad.voices.toMutableList()

        val voiceUiState = voices[index]

        if (currentIndex != index) {
            voices = voices.map { it.copy(currentProgress = 0, isPlaying = false) }.toMutableList()
            save(notepad.copy(voices = voices))
        }
        currentIndex = index
        playJob = viewModelScope.launch {
            voicePlayer.playMusic(voiceUiState.voiceName, voiceUiState.currentProgress.toInt())
                .collectLatest { currentProgress ->

                    voices = notepad.voices.toMutableList()

                    voices[index] =
                        notepad.voices[index].copy(
                            currentProgress = currentProgress.toLong(),
                            isPlaying = true,
                        )

                    save(notepad.copy(voices = voices))
                }
            voices = notepad.voices.toMutableList()
            voices[index] = voiceUiState.copy(currentProgress = 0, isPlaying = false)
            save(notepad.copy(voices = voices))
        }
    }

    fun pause() {
        // prevIndex=currentIndex
        val notepad = getNotePad()
        var voices = notepad.voices.toMutableList()

        val voiceUiState = voices[currentIndex]
        voices[currentIndex] = voiceUiState.copy(isPlaying = false)
        save(notepad.copy(voices = voices))
        playJob?.cancel()
        voicePlayer.pause()
    }
}
