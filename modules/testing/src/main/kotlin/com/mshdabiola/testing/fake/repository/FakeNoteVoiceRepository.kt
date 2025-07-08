package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.model.NoteVoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

internal class FakeNoteVoiceRepository
@Inject constructor() : NoteVoiceRepository {

    private val voiceNotesFlow = MutableStateFlow<LinkedHashMap<Long, NoteVoice>>(linkedMapOf())
    private val nextId = AtomicLong(1)

    override suspend fun upsert(voice: NoteVoice): Long {
        val idToUpsert = if (voice.id == -1L || !voiceNotesFlow.value.containsKey(voice.id)) {
            nextId.getAndIncrement()
        } else {
            voice.id
        }
        voiceNotesFlow.update {
            val mutableMap = it.toMutableMap()
            mutableMap[idToUpsert] = voice.copy(id = idToUpsert)
            LinkedHashMap(mutableMap)
        }
        return idToUpsert
    }

    override suspend fun upserts(voices: List<NoteVoice>): List<Long> {
        val ids = mutableListOf<Long>()

        voiceNotesFlow.update {
            val mutableMap = it.toMutableMap()
            voices.forEach { voice ->
                val idToUpsert = if (voice.id == -1L) {
                    nextId.getAndIncrement()
                } else {
                    voice.id
                }
                mutableMap[idToUpsert] = voice.copy(id = idToUpsert)
                ids.add(idToUpsert)
            }
            LinkedHashMap(mutableMap)
        }
        return ids
    }

    override suspend fun delete(id: Long) {
        println("Deleting voice note with ID: $id all  ${voiceNotesFlow.value}")
        voiceNotesFlow.update {
            val mutableMap = it.toMutableMap()
            mutableMap.remove(id)
            LinkedHashMap(mutableMap)
        }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        voiceNotesFlow.update {
            val mutableMap = it.toMutableMap()
            val idsToRemove = mutableMap.values.filter { it.noteId == noteId }.map { it.id }
            idsToRemove.forEach { id -> mutableMap.remove(id) }
            LinkedHashMap(mutableMap)
        }
    }

    override fun getAll(): Flow<List<NoteVoice>> {
        return voiceNotesFlow.asStateFlow().map { it.values.toList() }
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteVoice>> {
        return voiceNotesFlow.asStateFlow().map { map ->
            map.values.filter { it.noteId == noteId }.toList()
        }
    }

    override fun get(id: Long): Flow<NoteVoice?> {
        return voiceNotesFlow.asStateFlow().map { it[id] }
    }
}
