package com.mshdabiola.data.repository

import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.data.model.toEntity
import com.mshdabiola.data.model.toNotificationUiState
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.model.NotificationUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealNotificationRepository
@Inject constructor(
    private val notificationDao: NoteNotificationDao,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : NoteNotificationRepository {
    override suspend fun upserts(notifications: List<NotificationUiState>): List<Long> {
        return withContext(dispatcher) {
            notificationDao.upserts(notifications.map { it.toEntity() })
        }
    }

    override suspend fun upsert(notification: NotificationUiState): Long {
        return withContext(dispatcher) {
            notificationDao.upsert(notification.toEntity())
        }
    }

    override suspend fun delete(id: Long) {
        withContext(dispatcher) {
            notificationDao.delete(id)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        withContext(dispatcher) {
            notificationDao.deleteByNoteId(noteId)
        }
    }

    override fun getAll(): Flow<List<NotificationUiState>> {
        return notificationDao.getAll().map {
            it.map { it.toNotificationUiState() }
        }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NotificationUiState>> {
        return notificationDao.getByNoteId(noteId).map {
            it.map { it.toNotificationUiState() }
        }
    }

    override fun get(id: Long): Flow<NotificationUiState?> {
        return notificationDao.get(id).map {
            it?.toNotificationUiState()
        }
    }
}
