package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.LabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {

    @Upsert
    suspend fun upserts(labels: List<LabelEntity>): List<Long>

    @Upsert
    suspend fun upsert(label: LabelEntity): Long

    @Query("DELETE FROM label_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM label_table WHERE id = :id")
    fun get(id: Long): Flow<LabelEntity?>

    @Query("SELECT * FROM label_table")
    fun getAll(): Flow<List<LabelEntity>>
}
