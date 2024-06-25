package com.mshdabiola.database.model

import androidx.room.Embedded
import androidx.room.Relation

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
    val labels: List<NoteLabelEntity>,
)
