package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteNotificationDao {

    @Upsert
    suspend fun upserts(notifications: List<NotificationEntity>): List<Long>

    @Upsert
    suspend fun upsert(notification: NotificationEntity): Long

    @Query("DELETE FROM notification_table WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM notification_table WHERE note_id = :noteId")
    suspend fun deleteByNoteId(noteId: Long)

    @Query("SELECT * FROM notification_table WHERE id = :id")
    fun get(id: Long): Flow<NotificationEntity?>

    @Query("SELECT * FROM notification_table")
    fun getAll(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notification_table WHERE note_id = :noteId")
    fun getByNoteId(noteId: Long): Flow<List<NotificationEntity>>
}
