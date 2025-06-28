package com.mshdabiola.domain

import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteCheckRepository
import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetAllNoteUseCase
@Inject constructor(
    private val noteRepository: NoteRepository,
    private val noteCheckRepository: NoteCheckRepository,
    private val noteDrawingRepository: NoteDrawingRepository,
    private val noteImageRepository: NoteImageRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val noteNotificationRepository: NoteNotificationRepository,
    private val noteVoiceRepository: NoteVoiceRepository,
    private val labelRepository: LabelRepository,
    private val linkUriUseCase: LinkUriUseCase,
    private val contentManager: IContentManager,

) {
    operator fun invoke(noteDisplayCategory: NoteDisplayCategory): Flow<List<NotePad>> {
        val notes = when (noteDisplayCategory.noteType) {
            NoteType.LABEL -> combine(
                noteRepository.getAll(),
                noteLabelRepository.getByLabelId(noteDisplayCategory.labelId),

            ) { allNotes, labels ->
                allNotes.filter { note ->
                    labels.any { label ->
                        label.noteId == note.id
                    }
                }
            }

            NoteType.REMINDER -> {
                noteNotificationRepository.getAll()
                    .flatMapLatest { notifications ->
                        val noteIds = notifications.map { it.noteId }.toSet()
                        noteRepository.getByNoteIds(noteIds)
                    }
            }

            else -> noteRepository.getByNoteType(noteDisplayCategory.noteType)
        }

        return combine(
            notes,
            labelRepository.getAll(),
        ) { notes, allLabels ->
            notes.map { note ->

                val notification = noteNotificationRepository
                    .getByNoteId(note.id)
                    .first()
                    .firstOrNull()
                val drawings = noteDrawingRepository
                    .getByNoteId(note.id)
                    .first()
                val image = noteImageRepository
                    .getByNoteId(note.id)
                    .first()
                    .map {
                        it.copy(path = contentManager.getImagePath(it.id))
                    }
                val voices = noteVoiceRepository
                    .getByNoteId(note.id)
                    .first()
                val checks = noteCheckRepository
                    .getByNoteId(note.id)
                    .first()
                val labels = noteLabelRepository
                    .getByNoteId(note.id)
                    .first()
                    .map { label ->
                        allLabels.single { it.id == label.labelId }
                    }
                val uris = linkUriUseCase(note.detail, 1)

                NotePad(
                    note = note,
                    notification = notification,
                    drawings = drawings,
                    images = image,
                    voices = voices,
                    checks = checks,
                    labels = labels,
                    uris = uris,
                )
            }
        }
    }
}
