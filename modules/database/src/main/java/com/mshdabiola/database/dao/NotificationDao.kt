package com.mshdabiola.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mshdabiola.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Upsert
    suspend fun upsertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notification_table WHERE note_id = :noteId")
    fun getNotificationByNoteId(noteId: Long): Flow<NotificationEntity?>

    @Query("SELECT * FROM notification_table WHERE id = :notificationId")
    fun getNotificationById(notificationId: Long): Flow<NotificationEntity?>

    @Query("DELETE FROM notification_table WHERE id = :notificationId")
    suspend fun deleteNotificationById(notificationId: Long)

    @Query("DELETE FROM notification_table WHERE note_id = :noteId")
    suspend fun deleteNotificationsByNoteId(noteId: Long)
}
