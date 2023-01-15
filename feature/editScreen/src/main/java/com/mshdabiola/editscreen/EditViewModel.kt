package com.mshdabiola.editscreen


import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.AlarmManager
import com.mshdabiola.common.ContentManager
import com.mshdabiola.common.NotePlayer
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NoteLabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.database.repository.NoteVoiceRepository
import com.mshdabiola.designsystem.component.state.NoteCheckUiState
import com.mshdabiola.designsystem.component.state.NoteImageUiState
import com.mshdabiola.designsystem.component.state.NotePadUiState
import com.mshdabiola.designsystem.component.state.NoteTypeUi
import com.mshdabiola.designsystem.component.state.NoteVoiceUiState
import com.mshdabiola.designsystem.component.state.toNoteCheckUiState
import com.mshdabiola.designsystem.component.state.toNoteImageUiState
import com.mshdabiola.designsystem.component.state.toNotePad
import com.mshdabiola.designsystem.component.state.toNotePadUiState
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notePadRepository: NotePadRepository,
    private val contentManager: ContentManager,
    private val voicePlayer: NotePlayer,
    private val noteLabelRepository: NoteLabelRepository,
    private val labelRepository: LabelRepository,
    private val alarmManager: AlarmManager,
    private val noteVoiceRepository: NoteVoiceRepository,
    private val imageToText: ImageToText

) : ViewModel() {

    private val editArg = EditArg(savedStateHandle)
    var notePadUiState by mutableStateOf(NotePad().toNotePadUiState())
    var navigateToDrawing by mutableStateOf(false)

    private var photoId: Long = 0


    init {
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
                                noteId = notePad.note.id, focus = true
                            )
                        ).toImmutableList()
                    )
                }

                (-3).toLong() -> {
                    val notePad = getNewNotepad()
                    notePad.copy(
                        images = listOf(
                            NoteImageUiState(
                                id = getNewId(), noteId = notePad.note.id,
                                imageName = contentManager.getImagePath(editArg.data),
                                isDrawing = false
                            )
                        )
                            .toImmutableList()
                    )
                }


                (-4).toLong() -> {
                    val voicePath = contentManager.getVoicePath(editArg.data)
                    val length = getAudioLength(voicePath)
                    val notePad = getNewNotepad()
                    notePad.copy(
                        voices = listOf(
                            NoteVoiceUiState(
                                id = getNewId(), noteId = notePad.note.id,
                                voiceName = voicePath,
                                length = length,
                                currentProgress = 0f
                            )
                        )
                            .toImmutableList()
                    )

                }

                (-5).toLong() -> {
                    navigateToDrawing = true
                    getNewNotepad()
                }

                else -> {
                    val notePad = notePadRepository.getOneNotePad(editArg.id).toNotePadUiState()
                    val voices =
                        notePad.voices.map { it.copy(length = getAudioLength(it.voiceName)) }
                    val data = editArg.content
                    if (data == "extract") {
                        onImage(editArg.data.toInt())
                    }
                    notePad.copy(voices = voices.toImmutableList())

                }

            }
        }

        viewModelScope.launch {
            snapshotFlow {
                notePadUiState
            }

                .distinctUntilChanged { old, new -> old == new }
                .collectLatest {
                    //   Log.e("flow", "$it")
                    insertNotePad(it)
                }
        }

        viewModelScope.launch {

            labelRepository.getAllLabels()
                .distinctUntilChanged { old, new -> new == old }
                .collectLatest {
                    if (editArg.id > -1) {
                        val newNote =
                            notePadRepository.getOneNotePad(editArg.id).toNotePadUiState(it)
                        notePadUiState = notePadUiState.copy(labels = newNote.labels)
                    }
                }
        }

    }


    private suspend fun insertNotePad(notePad: NotePadUiState) {
        if (notePad.note.id != (-1L)) {
            notePadRepository.insertNotepad(notePad.toNotePad())
        }
    }

    private suspend fun getNewNotepad(): NotePadUiState {
        val notepad = NotePad()
        val id = notePadRepository.insertNotepad(notepad)
        return notepad.copy(note = notepad.note.copy(id = id)).toNotePadUiState()
    }

    private fun getNewId() = System.currentTimeMillis()

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
        //prevIndex=currentIndex
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
            NoteImage(id, notePadUiState.note.id, contentManager.getImagePath(id), false)
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState())

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
                contentManager.getImagePath(photoId),
                false
            )
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState())

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
            checks = noteChecks.toImmutableList()
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
            checks = emptyList<NoteCheckUiState>().toImmutableList()
        )

        viewModelScope.launch {
            notePadRepository.deleteNoteCheckByNoteId(notePadUiState.note.id)
        }

    }

    fun pinNote() {
        notePadUiState =
            notePadUiState.copy(note = notePadUiState.note.copy(isPin = !notePadUiState.note.isPin))
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
        val note = notePadUiState.note.copy(reminder = time, interval = interval ?: -1)
        notePadUiState = notePadUiState.copy(note = note)


        viewModelScope.launch {


            alarmManager.setAlarm(
                time,
                interval,
                requestCode = notePadUiState.note.id.toInt(),
                title = notePadUiState.note.title,
                content = notePadUiState.note.detail,
                noteId = notePadUiState.note.id
            )
        }

    }

    fun deleteAlarm() {
        val note = notePadUiState.note.copy(reminder = -1, interval = -1)
        notePadUiState = notePadUiState.copy(note = note)

        viewModelScope.launch {
            val id = note.id

            alarmManager.deleteAlarm(id.toInt())


        }
    }

    fun onArchive() {
        notePadUiState = if (notePadUiState.note.noteType == NoteTypeUi.ARCHIVE) {
            val note = notePadUiState.note.copy(noteType = NoteTypeUi.NOTE)
            notePadUiState.copy(note = note)
        } else {
            val note = notePadUiState.note.copy(noteType = NoteTypeUi.ARCHIVE)
            notePadUiState.copy(note = note)
        }
    }

    fun onDelete() {

        val note = notePadUiState.note.copy(noteType = NoteTypeUi.TRASH)
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
                checks = copy.checks.map { it.copy(noteId = newId) }
            )

            notePadRepository.insertNotepad(copy)


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
        }
    }

    private fun onImage(index: Int) {
        viewModelScope.launch {
            delay(1000)
            val image = notePadUiState.images[index]
            val text = imageToText.toText(image.imageName)
            val note = notePadUiState.note
            notePadUiState = notePadUiState.copy(note = note.copy(detail = "${note.detail}\n$text"))
        }

    }

}