package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.model.NoteVoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestNoteVoiceRepository : NoteVoiceRepository {

    // Use a MutableStateFlow to hold the voices, keyed by their ID for easy access.
    // This allows Flows to update automatically when the data changes.
    private val voicesFlow = MutableStateFlow<LinkedHashMap<Long, NoteVoice>>(linkedMapOf())
    private var nextId = 1L // For auto-incrementing IDs

    private fun updateVoices(newVoicesMap: LinkedHashMap<Long, NoteVoice>) {
        voicesFlow.value = newVoicesMap
    }

    override suspend fun upserts(voices: List<NoteVoice>): List<Long> {
        val currentVoices = voicesFlow.value.toMutableMap() as LinkedHashMap
        val ids = mutableListOf<Long>()
        voices.forEach { voice ->
            val idToUpsert: Long
            if (voice.id != 0L && currentVoices.containsKey(voice.id)) {
                // Update existing voice
                idToUpsert = voice.id
            } else {
                // Insert new voice
                idToUpsert = nextId++
            }
            currentVoices[idToUpsert] = voice.copy(id = idToUpsert)
            ids.add(idToUpsert)
        }
        // Ensure nextId is always greater than the max id after upserting
        val maxIdInUpsert = ids.maxOrNull() ?: (nextId - 1)
        nextId = maxOf(nextId, maxIdInUpsert + 1)

        updateVoices(currentVoices)
        return ids
    }

    override suspend fun upsert(voice: NoteVoice): Long {
        val currentVoices = voicesFlow.value.toMutableMap() as LinkedHashMap
        val idToUpsert: Long
        if (voice.id != -1L && currentVoices.containsKey(voice.id)) {
            // Update existing voice
            idToUpsert = voice.id
            currentVoices[idToUpsert] = voice // Assume voice is already copied or new if id was 0
        } else {
            // Insert new voice
            idToUpsert = nextId++
            currentVoices[idToUpsert] = voice.copy(id = idToUpsert)
        }
        nextId = maxOf(nextId, idToUpsert + 1) // Ensure nextId is correct
        updateVoices(currentVoices)
        return idToUpsert
    }

    override suspend fun delete(id: Long) {
        val currentVoices = voicesFlow.value.toMutableMap() as LinkedHashMap
        if (currentVoices.remove(id) != null) {
            updateVoices(currentVoices)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        val currentVoices = voicesFlow.value.toMutableMap() as LinkedHashMap
        val initialSize = currentVoices.size
        currentVoices.values.removeIf { it.noteId == noteId }
        if (currentVoices.size < initialSize) {
            updateVoices(currentVoices)
        }
    }

    override fun getAll(): Flow<List<NoteVoice>> {
        return voicesFlow.asStateFlow().map { it.values.toList().reversed() } // Often newest first
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoice>> {
        return voicesFlow.asStateFlow().map { map ->
            map.values.filter { it.noteId == noteId }.toList().reversed()
        }
    }

    override fun get(id: Long): Flow<NoteVoice?> {
        return voicesFlow.asStateFlow().map { it[id] }
    }

    // Helper function for testing to clear all data
    fun clearAllVoices() {
        updateVoices(linkedMapOf())
        nextId = 1L
    }

    // Helper function for testing to add voices directly
    fun addVoices(voicesToAdd: List<NoteVoice>) {
        val currentVoices = voicesFlow.value.toMutableMap() as LinkedHashMap
        voicesToAdd.forEach { voice ->
            val id = if (voice.id == 0L) nextId++ else voice.id
            currentVoices[id] = voice.copy(id = id)
            nextId = maxOf(nextId, id + 1)
        }
        updateVoices(currentVoices)
    }
}
