package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.model.NotificationUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNotificationRepository : NoteNotificationRepository {

    private val notificationsFlow = MutableStateFlow<LinkedHashMap<Long, NotificationUiState>>(linkedMapOf())
    private var nextId = 1L

    private fun updateNotifications(newMap: LinkedHashMap<Long, NotificationUiState>) {
        notificationsFlow.value = newMap
    }

    override suspend fun upserts(notifications: List<NotificationUiState>): List<Long> {
        val currentNotifications = notificationsFlow.value.toMutableMap() as LinkedHashMap
        val ids = mutableListOf<Long>()
        notifications.forEach { notification ->
            val idToUpsert: Long
            if (notification.noteId != -1L && currentNotifications.containsKey(notification.noteId)) {
                idToUpsert = notification.noteId
            } else {
                idToUpsert = nextId++
            }
            currentNotifications[idToUpsert] = notification.copy(noteId = idToUpsert)
            ids.add(idToUpsert)
        }
        val maxIdInUpsert = ids.maxOrNull() ?: (nextId - 1)
        nextId = maxOf(nextId, maxIdInUpsert + 1)
        updateNotifications(currentNotifications)
        return ids
    }

    override suspend fun upsert(notification: NotificationUiState): Long {
        val currentNotifications = notificationsFlow.value.toMutableMap() as LinkedHashMap
        val idToUpsert: Long
        if (notification.noteId != 0L && currentNotifications.containsKey(notification.noteId)) {
            idToUpsert = notification.noteId
            currentNotifications[idToUpsert] = notification
        } else {
            idToUpsert = nextId++
            currentNotifications[idToUpsert] = notification.copy(noteId = idToUpsert)
        }
        nextId = maxOf(nextId, idToUpsert + 1)
        updateNotifications(currentNotifications)
        return idToUpsert
    }

    override suspend fun delete(id: Long) {
        val currentNotifications = notificationsFlow.value.toMutableMap() as LinkedHashMap
        if (currentNotifications.remove(id) != null) {
            updateNotifications(currentNotifications)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentNotifications = notificationsFlow.value.toMutableMap() as LinkedHashMap
        val initialSize = currentNotifications.size
        currentNotifications.values.removeIf { it.noteId == noteId }
        if (currentNotifications.size < initialSize) {
            updateNotifications(currentNotifications)
        }
    }

    override fun getAll(): Flow<List<NotificationUiState>> {
        return notificationsFlow.asStateFlow().map { it.values.toList().reversed() }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NotificationUiState>> {
        return notificationsFlow.asStateFlow().map { map ->
            map.values.filter { it.noteId == noteId }.toList().reversed()
        }
    }

    override fun get(id: Long): Flow<NotificationUiState?> {
        return notificationsFlow.asStateFlow().map { it[id] }
    }

    // Helper for testing
    fun clearAll() {
        updateNotifications(linkedMapOf())
        nextId = 1L
    }

    fun addNotifications(items: List<NotificationUiState>) {
        val currentItems = notificationsFlow.value.toMutableMap() as LinkedHashMap
        items.forEach { item ->
            val id = if (item.noteId == 0L) nextId++ else item.noteId
            currentItems[id] = item.copy(noteId = id)
            nextId = maxOf(nextId, id + 1)
        }
        updateNotifications(currentItems)
    }
}
