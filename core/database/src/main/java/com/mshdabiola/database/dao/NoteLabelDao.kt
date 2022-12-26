package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteLabelEntity

@Dao
interface NoteLabelDao {

    @Upsert
    fun upsert(noteLabelEntity: NoteLabelEntity)
}