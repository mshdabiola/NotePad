/*
 *abiola 2022
 */

package com.mshdabiola.detail

import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.common.IContentManager
import com.mshdabiola.common.INotePlayer
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.DateUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationPlace
import com.mshdabiola.model.NotificationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val alarmManager: IAlarmManager,
    private val voicePlayer: INotePlayer,
    private val getNoteUseCase: GetNoteUseCase,
    private val addAllNoteUseCase: AddAllNoteUseCase,
    private val contentManager: IContentManager,
    private val dateUseCase: DateUseCase,

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
            getNoteUseCase(ll)
        }
    private val initState = DetailState(
        notePad = NotePad(
            note = Note(
                id = detailArg.id,
                color = detailArg.colorIndex,
                background = detailArg.background,
            ),
        ),
    )

    private var initTitle = false
    val detailState = combine(
        snapshotFlow { initState.title.text }
            .debounce(200),
        snapshotFlow { initState.detail.text }
            .debounce(200),
        currentNote,
    ) { title, content, notepad ->
        if (notepad == null) {
            val id = addAllNoteUseCase(NotePad(note = Note(id = -1)))
            currentNoteId.update {
                id
            }
            initState
        } else {
            if (!initTitle) {
                initState.title.edit {
                    append(notepad.note.title)
                }
                initState.detail.edit {
                    append(notepad.note.detail)
                }

                initTitle = true
            }
            if (title.isNotBlank() && content.isNotBlank() &&(title != notepad.note.title || content != notepad.note.detail)) {
                println("title $title content $content")
                addAllNoteUseCase(
                    notepad.copy(
                        note = notepad.note.copy(
                            title = title.toString(),
                            detail = content.toString(),
                        ),
                    ),
                )
            }
            initState.copy(
                notePad = notepad,
                updateAt = dateUseCase(notepad.note.editDate),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = initState,
    )

//    fun savNewNote() {
//        viewModelScope.launch {
//            val id = notePadRepository.upsert(NotePad(id = -1))
//
//        }
//    }

    private fun getNotePad(): NotePad {
        return detailState.value.notePad
    }

//    private fun save(notepad: NotePad) {
//        viewModelScope.launch {
//            notePadRepository.upsert(notepad)
//        }
//    }

    fun onCheckChange(text: String, id: Long) {
        viewModelScope.launch {
            val notepad = getNotePad()
            val noteChecks = notepad.checks.toMutableList()
            val index = noteChecks.indexOfFirst { it.id == id }
            val noteCheck = noteChecks[index].copy(content = text)
            noteChecks[index] = noteCheck
            addAllNoteUseCase(notepad.copy(checks = noteChecks))
        }
    }

    fun addCheck() {
        viewModelScope.launch {
            val notepad = getNotePad()
            val noteCheck = NoteCheck(isCheck = false)
            val noteChecks = notepad.checks.toMutableList()
            noteChecks.add(noteCheck)
            addAllNoteUseCase(notepad.copy(checks = noteChecks))
        }
    }

    fun onCheck(check: Boolean, id: Long) {
        viewModelScope.launch {
            val notepad = getNotePad()

            val noteChecks = notepad.checks.toMutableList()
            val index = noteChecks.indexOfFirst { it.id == id }
            val noteCheck = noteChecks[index].copy(isCheck = check)
            noteChecks[index] = noteCheck
            println(noteCheck)
            addAllNoteUseCase(notepad.copy(checks = noteChecks))
        }
    }

    fun onCheckDelete(id: Long) {
        viewModelScope.launch {
            val notepad = getNotePad()

            val noteChecks = notepad.checks.toMutableList()
            val index = noteChecks.indexOfFirst { it.id == id }
//            val noteCheck = noteChecks.removeAt(index)
//            viewModelScope.launch {
//                notePadRepository.deleteCheckNote(id, noteCheck.noteId)
//            }
            addAllNoteUseCase(notepad.copy(checks = noteChecks))
        }
    }

    fun changeToCheckBoxes() {
        viewModelScope.launch {
            val newNote = initState.detail.text.split("\n")

            initState.detail.clearText()
            val noteChecks = newNote.map { s ->
                NoteCheck(content = s, isCheck = false)
            }
            val notepad = getNotePad()

            addAllNoteUseCase(
                notepad.copy(
                    note = notepad.note.copy(
                        detail = "",
                        isCheck = true,
                    ),
                    checks = noteChecks,
                ),
            )
        }
    }

    fun unCheckAllItems() {
        viewModelScope.launch {
            val notepad = getNotePad()

            val noteChecks = notepad.checks.map { it.copy(isCheck = false) }
            addAllNoteUseCase(notepad.copy(checks = noteChecks))
        }
    }

    fun deleteCheckedItems() {
        viewModelScope.launch {
            val notepad = getNotePad()

            val checkNote = notepad.checks.filter { it.isCheck }
            val notCheckNote = notepad.checks.filter { !it.isCheck }
//            viewModelScope.launch {
//                checkNote.forEach {
//                    notePadRepository.deleteCheckNote(it.id, it.noteId)
//                }
//            }
            addAllNoteUseCase(notepad.copy(checks = notCheckNote))
        }
    }

    fun hideCheckBoxes() {
        viewModelScope.launch {
            val notepad = getNotePad()

            val noteCheck = notepad.checks.joinToString(separator = "\n") { it.content }

            initState.detail.edit {
                append(noteCheck)
            }

            addAllNoteUseCase(
                notepad.copy(
                    note = notepad.note.copy(
                        detail = noteCheck,
                        isCheck = false,
                    ),
                    checks = emptyList(),
                ),
            )
        }
    }

    fun pinNote() {
        viewModelScope.launch {
            val notepad = getNotePad()

            addAllNoteUseCase(notepad.copy(note = notepad.note.copy(isPin = !notepad.note.isPin)))
        }
    }

    fun onColorChange(index: Int) {
        viewModelScope.launch {
            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(note = notepad.note.copy(color = index)))
        }
    }

    fun onImageChange(index: Int) {
        viewModelScope.launch {
            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(note = notepad.note.copy(background = index)))
        }
    }

    fun onArchive() {
        viewModelScope.launch {
            val notepad = getNotePad()

            val newNote = if (notepad.note.noteType == NoteType.ARCHIVE) {
                notepad.copy(note = notepad.note.copy(noteType = NoteType.NOTE))
            } else {
                notepad.copy(note = notepad.note.copy(noteType = NoteType.ARCHIVE))
            }

            addAllNoteUseCase(newNote)
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(note = notepad.note.copy(noteType = NoteType.TRASH)))
        }
    }

    fun copyNote() {
        viewModelScope.launch {
            val note2 = getNotePad()

            val newNotePad = note2.copy(
                note = note2.note.copy(id = -1),
            )

            addAllNoteUseCase(newNotePad)
        }
    }

    fun deleteVoiceNote(index: Int) {
        viewModelScope.launch {
            val notepad = getNotePad()

            val voices = notepad.voices.toMutableList()
            val voice = voices.removeAt(index)

            addAllNoteUseCase(notepad.copy(voices = voices))
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
//        val note2 = notepad.value.copy(reminder = -1, interval = -1)
//        notepad.update {
//            note2
//        }
//
//        viewModelScope.launch {
//            alarmManager.deleteAlarm(note2.id.toInt())
//        }
    }

    fun setAlarm(time: Long, interval: Long?) {
//        val noteN = notepad.value.copy(
//            reminder = time,
//            interval = interval ?: -1,
//            reminderString = notePadRepository.dateToString(time),
//        )
//        notepad.update {
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
        viewModelScope.launch {
            val id = contentManager.saveImage(uri)

            val image = NoteImage(
                id = id,
                path = contentManager.getImagePath(id),
            )

            val notepad = getNotePad()

            addAllNoteUseCase(notepad.copy(images = notepad.images + image))
        }
    }

    fun saveVoice(uri: String, text: String) {
        viewModelScope.launch {
            val id = contentManager.saveVoice(uri)

            val voice = NoteVoice(
                id = id,
                voiceName = contentManager.getVoicePath(id),
            )
            initState.detail.edit {
                append(text)
            }

            val notepad = getNotePad()
            addAllNoteUseCase(notepad.copy(voices = notepad.voices + voice))
        }
    }

    fun getPhotoUri(): String {
        return contentManager.pictureUri()
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
            // save(notepad.copy(voices = voices))
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

                    //  save(notepad.copy(voices = voices))
                }
            voices = notepad.voices.toMutableList()
            voices[index] = voiceUiState.copy(currentProgress = 0, isPlaying = false)
            //  save(notepad.copy(voices = voices))
        }
        TODO("update voice ui state")
    }

    fun pause() {
        // prevIndex=currentIndex
        val notepad = getNotePad()
        var voices = notepad.voices.toMutableList()

        val voiceUiState = voices[currentIndex]
        voices[currentIndex] = voiceUiState.copy(isPlaying = false)
//        save(notepad.copy(voices = voices))
        playJob?.cancel()
        voicePlayer.pause()
    }
}
