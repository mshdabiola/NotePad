package com.mshdabiola.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.mshdabiola.model.NotePad

data class NotePadEntity(
    @Embedded
    val noteEntity: NoteEntity,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val images: List<NoteImageEntity>,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val voices: List<NoteVoiceEntity>,
    @Relation(parentColumn = "id", entityColumn = "noteId")
    val checks: List<NoteCheckEntity>,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val labels: List<NoteLabelEntity>
)

fun NotePadEntity.toNotePad() = NotePad(
    note = noteEntity.toNote(),
    images = images.map { it.toNoteImage() },
    voices = voices.map { it.toNoteVoice() },
    checks = checks.map { it.toNoteCheck() },
    labels = labels.map { it.toNoteLabel() }
)
