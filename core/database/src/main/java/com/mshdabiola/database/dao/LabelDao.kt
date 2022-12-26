package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteLabelEntity

@Dao
interface LabelDao {
    @Upsert
    fun upsert(labelEntity: NoteLabelEntity)
}