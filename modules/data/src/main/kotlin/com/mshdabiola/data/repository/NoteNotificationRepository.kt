package com.mshdabiola.data.repository

import com.mshdabiola.model.NotificationUiState
import kotlinx.coroutines.flow.Flow

interface NoteNotificationRepository {
    suspend fun upserts(notifications: List<NotificationUiState>): List<Long>

    suspend fun upsert(notification: NotificationUiState): Long
    suspend fun delete(id: Long)

    suspend fun deleteByNoteId(noteId: Long)

    fun getAll(): Flow<List<NotificationUiState>>
    fun getByNoteId(noteId: Long): Flow<List<NotificationUiState>>

    fun get(id: Long): Flow<NotificationUiState?>
}
