package com.mshdabiola.editscreen

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.AlarmManager
import com.mshdabiola.common.ContentManager
import com.mshdabiola.common.DateShortStringUsercase
import com.mshdabiola.common.DateStringUsercase
import com.mshdabiola.common.NotePlayer
import com.mshdabiola.common.Time12UserCase
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.database.repository.NoteVoiceRepository
import com.mshdabiola.designsystem.component.state.DateDialogUiData
import com.mshdabiola.designsystem.component.state.DateListUiState
import com.mshdabiola.designsystem.component.state.NoteCheckUiState
import com.mshdabiola.designsystem.component.state.NoteImageUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import com.mshdabiola.designsystem.component.state.NoteUiState
import com.mshdabiola.designsystem.component.state.NoteUriState
import com.mshdabiola.designsystem.component.state.NoteVoiceUiState
import com.mshdabiola.designsystem.component.state.Notify
import com.mshdabiola.designsystem.component.state.toNoteCheckUiState
import com.mshdabiola.designsystem.component.state.toNoteImageUiState
import com.mshdabiola.designsystem.component.state.toNotePad
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
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
import javax.inject.Inject
import kotlin.time.DurationUnit

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notePadRepository: NotePadRepository,
    private val contentManager: ContentManager,
    private val voicePlayer: NotePlayer,
    private val labelRepository: LabelRepository,
    private val alarmManager: AlarmManager,
    private val noteVoiceRepository: NoteVoiceRepository,
    private val imageToText: ImageToText,
    private val time12UserCase: Time12UserCase,
    private val dateStringUsercase: DateStringUsercase,
    private val dateShortStringUsercase: DateShortStringUsercase

) : ViewModel() {

    private val editArg = EditArg(savedStateHandle)
    var notePadUiState by mutableStateOf(NotePad().toNotePadUiState(getTime = dateShortStringUsercase::invoke, toPath = contentManager::getImagePath))
    var navigateToDrawing by mutableStateOf(false)
    private val _messages = MutableStateFlow(emptyList<Notify>().toImmutableList())
    val message = _messages.asStateFlow()


    private var photoId: Long = 0
    private var index = 0

    val regex =
        "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

    init {
        val text = "https://wwww.google.com/uru ikddhg iiso http://ggle.com https://wwww.google.com"
        val tex = text.split(" ")
            .filter { it.matches(regex.toRegex()) }
            .map { it.toUri().host }

        viewModelScope.launch {

            //   Log.e("Editviewmodel", "${editArg.id} ${editArg.content} ${editArg.data}")
            notePadUiState = when (editArg.id) {
                (-1).toLong() -> getNewNotepad()
                (-2).toLong() -> {
                    val notePad = getNewNotepad()
                    notePad.copy(
                        note = notePad.note.copy(isCheck = true),
                        checks = listOf(
                            NoteCheckUiState(
                                id = getNewId(),
                                noteId = notePad.note.id,
                                focus = true,
                            ),
                        ).toImmutableList(),
                    )
                }

                (-3).toLong() -> {
                    val notePad = getNewNotepad()
                    notePad.copy(
                        images = listOf(
                            NoteImageUiState(
                                id = editArg.data,
                                noteId = notePad.note.id,
                                path = contentManager.getImagePath(editArg.data),
                                isDrawing = false,
                            ),
                        )
                            .toImmutableList(),
                    )
                }

                (-4).toLong() -> {
                    val voicePath = contentManager.getVoicePath(editArg.data)
                    val length = getAudioLength(voicePath)
                    val notePad = getNewNotepad()
                    notePad.copy(
                        voices = listOf(
                            NoteVoiceUiState(
                                id = getNewId(),
                                noteId = notePad.note.id,
                                voiceName = voicePath,
                                length = length,
                                currentProgress = 0f,
                            ),
                        )
                            .toImmutableList(),
                    )
                }

                (-5).toLong() -> {

                    notePadUiState = getNewNotepad()
                    navigateToDrawing = true
                    notePadUiState
                }

                else -> {
                    val labels = labelRepository.getAllLabels().first()
                    val notePad = notePadRepository
                        .getOneNotePad(editArg.id)
                        .first()
                        .toNotePadUiState(labels, getTime = dateShortStringUsercase::invoke,contentManager::getImagePath)
                    val voices =
                        notePad.voices.map { it.copy(length = getAudioLength(it.voiceName)) }
                    val data = editArg.content
                    val padUiState=notePad.copy(voices = voices.toImmutableList())
                    if (data == "extract") {
                        onImage(contentManager.getImagePath(editArg.data),padUiState)
                        padUiState
                    }else{
                        padUiState
                    }

                }
            }

            //init Link in text
            computeUri(notePadUiState.note)

            //init bottom date
            launch(Dispatchers.IO) {
                initDate(notePadUiState.note)
            }

        }
        viewModelScope.launch{
            //on notepad image and labels change
            snapshotFlow {
                notePadUiState
            }
                .map { it.note.id }
                .distinctUntilChanged { old, new -> old == new }
                .collectLatest {
                    if (it>-1){

                        notePadRepository.getOneNotePad(it)
                            .mapNotNull { it.images to it.labels }
                            .distinctUntilChanged()
                            .collectLatest { pair ->

                                Timber.tag("editviewmodel").e(pair.first.joinToString())
                                val labels = labelRepository.getAllLabels().first()
                                val strLabel = pair.second.map { s ->
                                    labels.singleOrNull { it.id == s.labelId }?.label ?: ""
                                }
                                val image = pair.first.map { it.toNoteImageUiState(contentManager::getImagePath) }

                                notePadUiState = notePadUiState.copy(
                                    labels = strLabel.toImmutableList(),
                                    images = image.toImmutableList(),
                                )


                            }

                    }

                }

        }

        //save note
        viewModelScope.launch {
            snapshotFlow {
                notePadUiState
            }
                .distinctUntilChanged { old, new -> old == new }
                .collectLatest {
                    //   Log.e("flow", "$it")
                    insertNotePad(it)
                    //   computeUri(it.note)
                }
        }

    }

    private suspend fun insertNotePad(notePad: NotePadUiState) {
        if (!notePad.isEmpty()) {
            // Log.e("inset notepad", notePad.toString())
            val date = Clock.System.now().toEpochMilliseconds()
            notePadRepository.insertNotepad(
                notePad
                    .copy(
                        note = notePad.note.copy(editDate = date),
                    ).toNotePad(),
            )
        }
    }

    private suspend fun computeUri(notepad: NoteUiState) = withContext(Dispatchers.IO) {
        if (notepad.detail.contains(regex.toRegex())) {
            val uri = notepad.detail.split("\\s".toRegex())
                .filter { it.trim().matches(regex.toRegex()) }
                .mapIndexed { index, s ->
                    val path = s.toUri().authority ?: ""
                    val icon = "https://icon.horse/icon/$path"
                    NoteUriState(
                        id = index,
                        icon = icon,
                        path = path,
                        uri = s,
                    )
                }
                .toImmutableList()
            notePadUiState = notePadUiState.copy(uris = uri)
        }
    }

    private suspend fun getNewNotepad(): NotePadUiState {
        val notepad = NotePad()
        val id = notePadRepository.insertNotepad(notepad)
        return NotePadUiState(note = NoteUiState(id = id))
    }

    private fun getNewId(): Long {
        index += 1
        return System.currentTimeMillis() + index
    }

    fun onTitleChange(title: String) {
        val note = notePadUiState.note.copy(title = title)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun onDetailChange(detail: String) {
        val note = notePadUiState.note.copy(detail = detail)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun addCheck() {
        val noteId = notePadUiState.note.id
        val noteCheck = NoteCheckUiState(id = getNewId(), noteId = noteId, focus = true)

        val noteChecks = notePadUiState.checks.map { it.copy(focus = false) }.toMutableList()
        noteChecks.add(noteCheck)
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheckChange(value: String, id: Long) {
        val noteChecks = notePadUiState.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(content = value)
        noteChecks[index] = noteCheck
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheck(check: Boolean, id: Long) {
        val noteChecks = notePadUiState.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks[index].copy(isCheck = check)
        noteChecks[index] = noteCheck
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun onCheckDelete(id: Long) {
        val noteChecks = notePadUiState.checks.toMutableList()
        val index = noteChecks.indexOfFirst { it.id == id }
        val noteCheck = noteChecks.removeAt(index)
        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
        viewModelScope.launch {
            notePadRepository.deleteCheckNote(id, noteCheck.noteId)
        }
    }

    private var playJob: Job? = null
    private var currentIndex = -1
    fun playMusic(index: Int) {
        playJob?.cancel()
        val voiceUiState = notePadUiState.voices[index]
        var voices = notePadUiState.voices.toMutableList()

        if (currentIndex != index) {
            voices = voices.map { it.copy(currentProgress = 0f, isPlaying = false) }.toMutableList()
            notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
        }
        currentIndex = index
        playJob = viewModelScope.launch {
            voicePlayer.playMusic(voiceUiState.voiceName, voiceUiState.currentProgress.toInt())
                .collect {
                    voices[index] =
                        voiceUiState.copy(currentProgress = it.toFloat(), isPlaying = true)

                    notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
                }
            voices[index] = voiceUiState.copy(currentProgress = 0f, isPlaying = false)
            notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
        }
    }

    fun pause() {
        // prevIndex=currentIndex
        val voiceUiState = notePadUiState.voices[currentIndex]
        val voices = notePadUiState.voices.toMutableList()
        voices[currentIndex] = voiceUiState.copy(isPlaying = false)
        notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())
        playJob?.cancel()
        voicePlayer.pause()
    }

    fun saveImage(uri: Uri, id: Long) {
        contentManager.saveImage(uri, id)

        val noteImage =
            NoteImage(id, notePadUiState.note.id, false)
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState(contentManager::getImagePath))

        notePadUiState = notePadUiState.copy(images = listImage.toImmutableList())
    }

    fun saveVoice(uri: Uri, content: String, id: Long) {
        contentManager.saveVoice(uri, id)

        val voicePath = contentManager.getVoicePath(id)
        val length = getAudioLength(voicePath)
        val noteVoice =
            NoteVoiceUiState(id, notePadUiState.note.id, voicePath, length, 0f)
        val listVoices = notePadUiState.voices.toMutableList()
        listVoices.add(noteVoice)

        val note = notePadUiState.note.copy(detail = notePadUiState.note.detail + "\n" + content)

        notePadUiState = notePadUiState.copy(note = note, voices = listVoices.toImmutableList())
    }

    fun savePhoto() {
        val noteImage =
            NoteImage(
                photoId,
                notePadUiState.note.id,
                false,
            )
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState(contentManager::getImagePath))

        notePadUiState = notePadUiState.copy(images = listImage.toImmutableList())
    }

    fun getPhotoUri(): Uri {
        photoId = getNewId()
        return contentManager.pictureUri(photoId)
    }

    fun changeToCheckBoxes() {
        val newNote = notePadUiState.note.detail.split("\n")
        val id = notePadUiState.note.id
        val noteChecks = newNote.map { s ->
            NoteCheck(id = getNewId(), noteId = id, content = s).toNoteCheckUiState()
        }
        notePadUiState = notePadUiState.copy(
            note = notePadUiState.note.copy(isCheck = true, detail = ""),
            checks = noteChecks.toImmutableList(),
        )
    }

    fun unCheckAllItems() {
        val noteChecks = notePadUiState.checks.map { it.copy(isCheck = false) }

        notePadUiState = notePadUiState.copy(checks = noteChecks.toImmutableList())
    }

    fun deleteCheckedItems() {
        val checkNote = notePadUiState.checks.filter { it.isCheck }
        val notCheckNote = notePadUiState.checks.filter { !it.isCheck }

        notePadUiState = notePadUiState.copy(checks = notCheckNote.toImmutableList())

        viewModelScope.launch {
            checkNote.forEach {
                notePadRepository.deleteCheckNote(it.id, it.noteId)
            }
        }
    }

    fun hideCheckBoxes() {
        val noteCheck = notePadUiState.checks.joinToString(separator = "\n") { it.content }

        notePadUiState = notePadUiState.copy(
            note = notePadUiState.note.copy(detail = noteCheck, isCheck = false),
            checks = emptyList<NoteCheckUiState>().toImmutableList(),
        )

        viewModelScope.launch {
            notePadRepository.deleteNoteCheckByNoteId(notePadUiState.note.id)
        }
    }

    fun pinNote() {
        notePadUiState =
            notePadUiState.copy(note = notePadUiState.note.copy(isPin = !notePadUiState.note.isPin))

        if (notePadUiState.note.isPin){
            addNotify("Note is pinned")
        }else{
            addNotify("Note is not pinned")
        }

    }

    fun onColorChange(index: Int) {
        val note = notePadUiState.note.copy(color = index)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun onImageChange(index: Int) {
        val note = notePadUiState.note.copy(background = index)
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun setAlarm(time: Long, interval: Long?) {
        val note = notePadUiState.note.copy(reminder = time, interval = interval ?: -1, date = dateShortStringUsercase(time))
        notePadUiState = notePadUiState.copy(note = note)

        viewModelScope.launch {
            alarmManager.setAlarm(
                time,
                interval,
                requestCode = notePadUiState.note.id.toInt(),
                title = notePadUiState.note.title,
                content = notePadUiState.note.detail,
                noteId = notePadUiState.note.id,
            )
        }
    }

    fun deleteAlarm() {
        val note = notePadUiState.note.copy(reminder = -1, interval = -1)
        notePadUiState = notePadUiState.copy(note = note)

        viewModelScope.launch {
            val id = note.id

            alarmManager.deleteAlarm(id.toInt())
            addNotify("Alarm deleted")
        }
    }

    fun onArchive() {
        notePadUiState = if (notePadUiState.note.noteType.type == NoteType.ARCHIVE) {
            val note = notePadUiState.note.copy(noteType = NoteTypeUi())
            addNotify("Note archived")
            notePadUiState.copy(note = note)
        } else {
            val note = notePadUiState.note.copy(noteType = NoteTypeUi(NoteType.ARCHIVE))
            addNotify("Note already archived")
            notePadUiState.copy(note = note)
        }
    }

    fun onDelete() {
        val note = notePadUiState.note.copy(noteType = NoteTypeUi(NoteType.TRASH))
        notePadUiState = notePadUiState.copy(note = note)
    }

    fun copyNote() {
        viewModelScope.launch {
            val notepads = notePadUiState.toNotePad()

            var copy = notepads.copy(note = notepads.note.copy(id = null))

            val newId = notePadRepository.insertNotepad(copy)

            copy = copy.copy(
                note = copy.note.copy(id = newId),
                images = copy.images.map { it.copy(noteId = newId) },
                voices = copy.voices.map { it.copy(noteId = newId) },
                labels = copy.labels.map { it.copy(noteId = newId) },
                checks = copy.checks.map { it.copy(noteId = newId) },
            )

            notePadRepository.insertNotepad(copy)
            addNotify("Note copied")
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
            val voices = notePadUiState.voices.toMutableList()
            val voice = voices.removeAt(index)
            notePadUiState = notePadUiState.copy(voices = voices.toImmutableList())

            noteVoiceRepository.delete(voice.id)

            addNotify("Voice note deleted")
        }
    }

    private fun onImage(path: String,notePad: NotePadUiState) {
        viewModelScope.launch {
            try {
               // val image = notePad.images[index]
                val text = try {
                    imageToText.toText(path)
                }catch (e:Exception){
                    e.printStackTrace()
                    ""
                }
                val note = notePad.note
                notePadUiState = notePadUiState.copy(note = note.copy(detail = "${note.detail}\n$text"))
                addNotify("Image text extracted")
            }catch (e:Exception){
                e.printStackTrace()
                addNotify("Error occur during extract of image")
            }

        }
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
    private var currentLocalDate = LocalDate(1, 2, 3)

    //date and time dialog logic

    private fun initDate(note: NoteUiState) {
        val now = Clock.System.now()
        today = now.toLocalDateTime(TimeZone.currentSystemDefault())
        currentDateTime =
            if (note.reminder > 0) Instant.fromEpochMilliseconds(note.reminder).toLocalDateTime(
                TimeZone.currentSystemDefault()
            ) else today
        currentLocalDate=currentDateTime.date


        val timeList = listOf(
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
                if (index != timeList.lastIndex) {

                    val greater = timeList[index] > today.time
                    dateListUiState.copy(
                        enable = greater,
                        value = time12UserCase(timeList[index]),
                        trail = time12UserCase(timeList[index])
                    )
                } else {
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
        val interval = when (note.interval) {
           DateTimeUnit.HOUR.times(24).duration.toLong(DurationUnit.MILLISECONDS)->1

            DateTimeUnit.HOUR.times(24 * 7).duration.toLong(DurationUnit.MILLISECONDS)->2

            DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)->3

            DateTimeUnit.HOUR.times(24 * 7 * 30).duration.toLong(DurationUnit.MILLISECONDS)->4
            else->0
        }




        _dateTimeState.update {
            it.copy(
                isEdit = note.reminder > 0,
                currentTime = if (note.reminder>0) timeList.lastIndex else 0,
                timeData = timeList,
                timeError = today>currentDateTime,
                currentDate = if (note.reminder>0) datelist.lastIndex else 0,
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
            val time=timeList[dateTimeState.value.currentTime]
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
                timeList[index].hour,
                timeList[index].minute
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
            //Timber.tag("editv").e("Set Alarm")
            addNotify("Alarm is set")
        }else{
            //Timber.tag("editv").e("Alarm not set " + now + " " + setime)
            addNotify("Alarm not set, time as past")
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
            val time=timeList[dateTimeState.value.currentTime]
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

        timeList[timeList.lastIndex] = time
        val date = when (dateTimeState.value.currentDate) {
            0 -> today.date
            1 -> today.date.plus(1, DateTimeUnit.DAY)
            else -> currentLocalDate
        }
        val datetime=LocalDateTime(date,time)

        Timber.tag("onSettime").e("current " + today + " date " + datetime)


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
        val notifies = _messages.value.toMutableList()

        notifies.add(Notify(message = text, callback = ::onNotifyDelive))
        _messages.update {
            notifies.toImmutableList()
        }
    }

    private fun onNotifyDelive() {

        val notifies = _messages.value.toMutableList()

        notifies.removeFirst()
        _messages.update {
            notifies.toImmutableList()
        }
    }



}
