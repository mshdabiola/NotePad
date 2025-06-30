package com.mshdabiola.domain

import com.mshdabiola.data.repository.NoteCheckRepository
import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.model.NotePad
import kotlinx.datetime.Clock
import javax.inject.Inject

class AddAllNoteUseCase
@Inject constructor(
    private val noteRepository: NoteRepository,
    private val noteCheckRepository: NoteCheckRepository,
    private val noteDrawingRepository: NoteDrawingRepository,
    private val noteImageRepository: NoteImageRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val noteNotificationRepository: NoteNotificationRepository,
    private val noteVoiceRepository: NoteVoiceRepository,

) {
    suspend operator fun invoke(notePad: NotePad): Long {
//        check(!notePad.isEmpty())

        val now = Clock.System.now().toEpochMilliseconds()

        var id = noteRepository.upsert(notePad.note.copy(editDate = now))

        if (id == -1L) {
            id = notePad.note.id
        }
        if (notePad.voices.isNotEmpty()) {
            noteVoiceRepository.upserts(
                notePad.voices.map { it.copy(noteId = id) },
            )
        }

        if (notePad.drawings.isNotEmpty()) {
            noteDrawingRepository.upserts(
                notePad.drawings.map { it.copy(noteId = id) },
            )
        }
        if (notePad.images.isNotEmpty()) {
            noteImageRepository.upserts(
                notePad.images.map { it.copy(noteId = id) },
            )
        }
        if (notePad.checks.isNotEmpty()) {
            noteCheckRepository.upserts(
                notePad.checks.map { it.copy(noteId = id) },
            )
        }
        if (notePad.labels.isNotEmpty()) {
            noteLabelRepository.upserts(
                notePad.labels.map { NoteLabel(noteId = id, labelId = it.id) },
            )
        }
        if (notePad.notification != null) {
            noteNotificationRepository.upsert(
                notePad.notification!!.copy(noteId = id),
            )
        }

        return id
    }
}
