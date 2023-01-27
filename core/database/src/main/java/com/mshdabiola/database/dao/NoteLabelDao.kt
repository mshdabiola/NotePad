package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NoteLabelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLabelDao {

    @Upsert
    suspend fun upsert(noteLabelEntity: List<NoteLabelEntity>)

    @Query("DELETE FROM note_label_table WHERE noteId IN (:ids) AND labelId = :labelId")
    suspend fun delete(ids: Set<Long>, labelId: Long)

    @Query("DELETE FROM note_label_table WHERE labelId = :id")
    suspend fun deleteByLabelId(id: Long)

    @Query("DELETE FROM note_label_table WHERE noteId = :id")
    suspend fun deleteByNoteId(id: Long)

    @Query("SELECT * FROM note_label_table WHERE noteId = :id")
    fun getAll(id: Long): Flow<List<NoteLabelEntity>>

//    @Query("SELECT noteId, label, COUNT(label) FROM note_label_table WHERE noteId = :ids GROUP BY label  ")
//    suspend fun getIdAndNot(ids : Set<Long>)
}
