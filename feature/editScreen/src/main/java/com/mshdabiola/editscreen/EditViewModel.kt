package com.mshdabiola.editscreen

//import android.util.Log
import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
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
import com.mshdabiola.designsystem.component.state.NoteUiState
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
                (-1).toLong() -> NotePadUiState(note = NoteUiState(focus = true))
                (-2).toLong() -> NotePadUiState(
                    note = NoteUiState(isCheck = true), checks = listOf(
                        NoteCheckUiState(
                            id = 0,
                            noteId = -1, focus = true
                        )
                    ).toImmutableList()
                )

                (-3).toLong() ->
                    NotePadUiState(
                        images = listOf(
                            NoteImageUiState(
                                id = 0, noteId = -1,
                                imageName = contentManager.getImagePath(editArg.data),
                                isDrawing = false
                            )
                        )
                            .toImmutableList()
                    )


                (-4).toLong() -> {

                    val voicePath = contentManager.getVoicePath(editArg.data)
                    val length = getAudioLength(voicePath)
                    NotePadUiState(
                        voices = listOf(
                            NoteVoiceUiState(
                                id = 0, noteId = -1,
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
                    NotePadUiState(note = NoteUiState(detail = "image"))
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


        if (notePad.note.title.isNotBlank()
            || notePad.note.detail.isNotBlank()
            || notePad.checks.isNotEmpty()
            || notePad.voices.isNotEmpty()
            || notePad.images.isNotEmpty()
        ) {
            if (notePad.note.id == null) {
                val id = notePadRepository.insertNotepad(notePad.toNotePad())
                savedStateHandle[noteId] = id
                val note = notePadUiState.note.copy(id = id)
                val notechecks = notePad.checks.toMutableList()
                if (notechecks.isNotEmpty()) {

                    notechecks[0] = notechecks[0].copy(noteId = id)
                }
                val noteImages = notePad.images.toMutableList()
                if (noteImages.isNotEmpty()) {

                    noteImages[0] = noteImages[0].copy(noteId = id)
                }
                val noteVoice = notePad.voices.toMutableList()
                if (noteVoice.isNotEmpty()) {

                    noteVoice[0] = noteVoice[0].copy(noteId = id)
                }

                notePadUiState = notePadUiState.copy(
                    note = note,
                    checks = notechecks.toImmutableList(),
                    images = noteImages.toImmutableList(),
                    voices = noteVoice.toImmutableList()
                )

                //noteState = noteUiState.copy(id = id)

            } else {
                notePadRepository.insertNotepad(notePad.toNotePad())

            }
        }

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
        val size = (notePadUiState.checks.lastOrNull()?.id ?: -1) + 1
        val noteId = notePadUiState.note.id
        val noteCheck = NoteCheckUiState(id = size, noteId = noteId ?: -1, focus = true)

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
                    Log.e("flow", "time is $it")

                    voices[index] =
                        voiceUiState.copy(currentProgress = it.toFloat(), isPlaying = true)
                    Log.e("flow", "time is ${it}")
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

        val size = (notePadUiState.images.lastOrNull()?.id ?: -1) + 1
        val noteImage =
            NoteImage(size, notePadUiState.note.id ?: -1, contentManager.getImagePath(id), false)
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState())

        notePadUiState = notePadUiState.copy(images = listImage.toImmutableList())


    }

    fun saveVoice(uri: Uri, content: String, id: Long) {
        contentManager.saveVoice(uri, id)

        val size = (notePadUiState.voices.lastOrNull()?.id ?: -1) + 1
        val voicePath = contentManager.getVoicePath(id)
        val length = getAudioLength(voicePath)
        val noteVoice =
            NoteVoiceUiState(size, notePadUiState.note.id ?: -1, voicePath, length, 0f)
        val listVoices = notePadUiState.voices.toMutableList()
        listVoices.add(noteVoice)

        val note = notePadUiState.note.copy(detail = notePadUiState.note.detail + "\n" + content)



        notePadUiState = notePadUiState.copy(note = note, voices = listVoices.toImmutableList())


    }

    fun savePhoto() {

        val size = (notePadUiState.images.lastOrNull()?.id ?: -1) + 1
        val noteImage =
            NoteImage(
                size,
                notePadUiState.note.id ?: -1,
                contentManager.getImagePath(photoId),
                false
            )
        val listImage = notePadUiState.images.toMutableList()
        listImage.add(noteImage.toNoteImageUiState())

        notePadUiState = notePadUiState.copy(images = listImage.toImmutableList())
    }

    fun getPhotoUri(): Uri {
        photoId = System.currentTimeMillis()
        return contentManager.pictureUri(photoId)
    }

    fun changeToCheckBoxes() {
        val newNote = notePadUiState.note.detail.split("\n")
        val id = notePadUiState.note.id ?: -1
        val noteChecks = newNote.mapIndexed { index, s ->
            NoteCheck(id = index.toLong(), noteId = id, content = s).toNoteCheckUiState()
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
            notePadRepository.deleteNoteCheckByNoteId(notePadUiState.note.id!!)
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
            if (note.id == null) {
                delay(200)
            }


            alarmManager.setAlarm(
                time,
                interval,
                requestCode = notePadUiState.note.id?.toInt() ?: -1,
                title = notePadUiState.note.title,
                content = notePadUiState.note.detail,
                noteId = notePadUiState.note.id ?: 0L
            )
        }

    }

    fun deleteAlarm() {
        val note = notePadUiState.note.copy(reminder = -1, interval = -1)
        notePadUiState = notePadUiState.copy(note = note)

        viewModelScope.launch {
            val id = note.id
            if (id != null) {
                alarmManager.deleteAlarm(id.toInt())
            }

        }
    }

    fun onArchive() {
        if (notePadUiState.note.noteType == NoteTypeUi.ARCHIVE) {
            val note = notePadUiState.note.copy(noteType = NoteTypeUi.NOTE)
            notePadUiState = notePadUiState.copy(note = note)
        } else {
            val note = notePadUiState.note.copy(noteType = NoteTypeUi.ARCHIVE)
            notePadUiState = notePadUiState.copy(note = note)
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

    fun onImage(index: Int) {
        viewModelScope.launch {
            delay(1000)
            val image = notePadUiState.images[index]
            val text = imageToText.toText(image.imageName)
            val note = notePadUiState.note
            notePadUiState = notePadUiState.copy(note = note.copy(detail = "${note.detail}\n$text"))
        }

    }

}