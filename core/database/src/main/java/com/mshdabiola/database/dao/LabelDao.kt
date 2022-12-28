package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Upsert
    suspend fun upsert(labelEntity: List<LabelEntity>)

    @Query("DELETE FROM label_table WHERE id = :id")
    suspend fun delete(id: Long)


    @Query("SELECT * FROM label_table")
    fun getAllLabel(): Flow<LabelEntity>

    @Query("SELECT * FROM label_table")
    suspend fun getAllLabels(): List<LabelEntity>

}