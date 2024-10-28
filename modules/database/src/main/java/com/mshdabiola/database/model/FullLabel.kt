package com.mshdabiola.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class FullLabel(
    @Embedded
    val noteLabel: NoteLabelEntity,
    @Relation(entity = LabelEntity::class, entityColumn = "id", parentColumn = "labelId")
    val label: LabelEntity,
)
