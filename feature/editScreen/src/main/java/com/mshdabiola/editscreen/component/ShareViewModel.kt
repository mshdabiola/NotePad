package com.mshdabiola.editscreen.component

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mshdabiola.common.ContentManager
import com.mshdabiola.database.repository.LabelRepository
import com.mshdabiola.database.repository.NotePadRepository
import com.mshdabiola.editscreen.LabelUiState
import com.mshdabiola.editscreen.toLabelUiState
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.model.NotePad
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val labelRepository: LabelRepository,
    private val notePadRepository: NotePadRepository,
    private val contentManager: ContentManager
) : ViewModel() {
    private val c = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val coroutineScope = CoroutineScope(c)

    var labels by mutableStateOf(emptyList<LabelUiState>().toImmutableList())
    var selectLabels by mutableStateOf(emptyList<LabelUiState>().toImmutableList())

    var showLabel by mutableStateOf(false)

    init {
        viewModelScope.launch {
            labels = labelRepository
                .getAllLabels()
                .first()
                .map {
                    it.toLabelUiState()
                }
                .toImmutableList()

            showLabel = labels.isNotEmpty()
        }

        viewModelScope.launch {
            snapshotFlow { labels }
                .distinctUntilChanged()
                .collectLatest { it ->
                    selectLabels = it.filter { it.isCheck }.toImmutableList()
                }
        }

    }

    fun toggleLabel(id: Long) {
        val labelsMutableList = labels.toMutableList()
        val index = labelsMutableList.indexOfFirst { it.id == id }
        var label = labelsMutableList[index]
        label = label.copy(isCheck = !label.isCheck)
        labelsMutableList[index] = label
        labels = labelsMutableList.toImmutableList()
    }

    fun saveNote(title: String, subject: String, images: List<Uri>) {
        coroutineScope.launch {
            val noteId = notePadRepository.insertNote(Note())
            val noteImages = images
                .map {
                    val time = System.currentTimeMillis()
                    contentManager.saveImage(it, time)
                    val path = contentManager.getImagePath(time)
                    NoteImage(time, noteId, path, isDrawing = false)
                }
            val labels = selectLabels
                .map { NoteLabel(noteId, it.id) }
            val notePad = NotePad(
                note = Note(
                    id = noteId, title, subject,
                    editDate = Clock.System.now().toEpochMilliseconds()
                ),
                images = noteImages,
                labels = labels
            )

            notePadRepository.insertNotepad(notePad)
        }


    }

}