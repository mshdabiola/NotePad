package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteLabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLabelDao {

    @Upsert
    suspend fun upserts(labels: List<NoteLabelEntity>): List<Long>

    @Upsert
    suspend fun upsert(label: NoteLabelEntity): Long

    @Query("DELETE FROM note_label_table WHERE noteId = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("DELETE FROM note_label_table WHERE noteId = :noteId AND labelId = :labelId")
    suspend fun deleteByNoteIdAndLabelId(noteId: Long, labelId: Long)

    @Query("SELECT * FROM note_label_table")
    fun getAll(): Flow<List<NoteLabelEntity>>

    @Query("SELECT * FROM note_label_table WHERE noteId = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NoteLabelEntity>>

    @Query("SELECT * FROM note_label_table WHERE labelId = :labelId")
    fun getByLabelId(labelId: Long): Flow<List<NoteLabelEntity>>

    @Query("SELECT * FROM note_label_table WHERE noteId IN (:ids)")
    fun getByNoteIds(ids: Set<Long>): Flow<List<NoteLabelEntity>>
}
