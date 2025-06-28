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
import com.mshdabiola.model.NotePad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetNoteUseCase
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
    private val audioLengthUseCase: AudioLengthUseCase,

) {
    operator fun invoke(id: Long): Flow<NotePad?> {
        val notes = noteRepository.get(id)

        return combine(
            notes,
            labelRepository.getAll(),
        ) { note, allLabels ->
            val notepad = if (note == null) {
                null
            } else {
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
                    .map {
                        it.copy(
                            voiceName = contentManager.getVoicePath(it.id),
                            length = audioLengthUseCase(contentManager.getVoicePath(it.id)),
                        )
                    }
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

            notepad
        }
    }
}
