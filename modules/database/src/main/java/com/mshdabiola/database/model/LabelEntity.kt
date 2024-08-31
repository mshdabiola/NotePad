package com.mshdabiola.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "label_table")
data class LabelEntity(
    @PrimaryKey
    val id: Long?,
    val name: String,
)
