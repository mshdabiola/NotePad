package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.model.NotificationUiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FakeNotificationRepository
@Inject constructor() : NoteNotificationRepository {
    override suspend fun upserts(notifications: List<NotificationUiState>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(notification: NotificationUiState): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll(): Flow<List<NotificationUiState>> {
        TODO("Not yet implemented")
    }

    override fun getByNoteId(noteId: Long): Flow<List<NotificationUiState>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Long): Flow<NotificationUiState?> {
        TODO("Not yet implemented")
    }
}
