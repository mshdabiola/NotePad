package com.mshdabiola.database.model

import androidx.room.Entity

@Entity(
    tableName = "note_label_table",
    primaryKeys = ["noteId", "labelId"],
)
data class NoteLabelEntity(
    val noteId: Long,
    val labelId: Long,
)
