package com.mshdabiola.domain

import com.mshdabiola.common.IContentManager
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.NotePad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetNoteUseCase
@Inject constructor(
    private val noteRepository: NoteRepository,
    private val linkUriUseCase: LinkUriUseCase,
    private val contentManager: IContentManager,
    private val audioLengthUseCase: AudioLengthUseCase,

) {
    operator fun invoke(id: Long): Flow<NotePad?> {
        return noteRepository.get(id)
            .mapLatest {
                it?.copy(
                    images = it.images.map { image ->
                        image.copy(path = contentManager.getImagePath(image.id))
                    },
                    uris = linkUriUseCase(it.note.detail, 10),
                    voices = it.voices.map { voice ->
                        val path = contentManager.getVoicePath(voice.id)
                        voice.copy(
                            filePath = path,
                            length = audioLengthUseCase(path),
                        )
                    },
                )
            }
    }
}
