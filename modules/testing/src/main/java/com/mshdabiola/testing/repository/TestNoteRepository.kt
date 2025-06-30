package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

internal class TestNoteRepository : NoteRepository {

    private val notesFlow = MutableStateFlow<LinkedHashMap<Long, Note>>(linkedMapOf())
    private var nextId = 1L

    // Helper function to get a snapshot of notes for operations
    private fun getNotesMap(): LinkedHashMap<Long, Note> = notesFlow.value

    // Helper to update the flow
    private fun updateNotes(newNotesMap: LinkedHashMap<Long, Note>) {
        notesFlow.value = newNotesMap
    }

    override suspend fun upserts(notes: List<Note>): List<Long> {
        val currentNotes = getNotesMap().toMutableMap() as LinkedHashMap // Make a mutable copy
        val ids = mutableListOf<Long>()
        notes.forEach { note ->
            val idToUpsert = if (note.id != 0L && currentNotes.containsKey(note.id)) {
                note.id // Update existing
            } else {
                nextId++ // Insert new
            }
            currentNotes[idToUpsert] = note.copy(id = idToUpsert)
            ids.add(idToUpsert)
            if (idToUpsert == nextId - 1) { // if it was a new insert, adjust nextId
                // No, nextId is already incremented, this logic is slightly off if note.id was 0 but an existing ID was reused.
                // Correct logic for nextId should just ensure it's always higher than any existing ID if we allow setting arbitrary IDs.
                // For simplicity, if note.id is 0, we assign nextId and increment.
                // If note.id is non-zero, we use it. If it's new and higher than nextId, update nextId.
            }
        }
        // Ensure nextId is always greater than the max id after upserting
        val maxIdInUpsert = ids.maxOrNull() ?: (nextId - 1)
        nextId = maxOf(nextId, maxIdInUpsert + 1)

        updateNotes(currentNotes)
        return ids
    }

    override suspend fun upsert(note: Note): Long {
        val currentNotes = getNotesMap().toMutableMap() as LinkedHashMap
        val idToUpsert: Long
        if (note.id != -1L && currentNotes.containsKey(note.id)) {
            // Update existing note
            idToUpsert = note.id
            currentNotes[idToUpsert] = note
        } else {
            // Insert new note
            idToUpsert = nextId++
            currentNotes[idToUpsert] = note.copy(id = idToUpsert)
        }
        nextId = maxOf(nextId, idToUpsert + 1) // Ensure nextId is correct
        updateNotes(currentNotes)
        return idToUpsert
    }

    override suspend fun delete(id: Long) {
        val currentNotes = getNotesMap().toMutableMap() as LinkedHashMap
        currentNotes.remove(id)
        updateNotes(currentNotes)
    }

    override suspend fun deleteIds(ids: Set<Long>) {
        val currentNotes = getNotesMap().toMutableMap() as LinkedHashMap
        ids.forEach { currentNotes.remove(it) }
        updateNotes(currentNotes)
    }

    override suspend fun deleteTrash() {
        val currentNotes = getNotesMap().toMutableMap() as LinkedHashMap
        val nonTrashedNotes = currentNotes.filterValues { it.noteType != NoteType.TRASH } as LinkedHashMap
        updateNotes(nonTrashedNotes)
    }

    // For Fake, NotePad will simply be the Note itself.
    // In a real scenario, NotePad would aggregate data from other repositories (checks, images etc.)
    private fun Note.toNotePad(): NotePad {
        // In a more complex fake, you might query fake check/image/label repositories here
        // based on this note's ID to construct a full NotePad.
        return NotePad(note = this, checks = emptyList(), images = emptyList(), labels = emptyList(), drawings = emptyList())
    }

    override fun getAll(): Flow<List<NotePad>> {
        return notesFlow.asStateFlow().map { notesMap ->
            notesMap.values.map { it.toNotePad() }.reversed() // Often newest first
        }
    }

    override fun get(id: Long): Flow<NotePad?> {
        return notesFlow.asStateFlow().map { notesMap ->
            notesMap[id]?.toNotePad()
        }
    }

    override fun getByNoteType(noteType: NoteType): Flow<List<NotePad>> {
        return notesFlow.asStateFlow().map { notesMap ->
            notesMap.values.filter { it.noteType == noteType }.map { it.toNotePad() }.reversed()
        }
    }

    override fun getByNoteIds(set: Set<Long>): Flow<List<NotePad>> {
        return notesFlow.asStateFlow().map { notesMap ->
            notesMap.values.filter { it.id in set }.map { it.toNotePad() }.reversed()
        }
    }

    // Helper for testing to clear all data
    fun clearAllNotes() {
        updateNotes(linkedMapOf())
        nextId = 1L
    }

    // Helper for testing to add notes directly
    fun addNotes(notesToAdd: List<Note>) {
        val currentNotes = getNotesMap().toMutableMap() as LinkedHashMap
        notesToAdd.forEach { note ->
            val id = if (note.id == 0L) nextId++ else note.id
            currentNotes[id] = note.copy(id = id)
            nextId = maxOf(nextId, id + 1)
        }
        updateNotes(currentNotes)
    }
}
