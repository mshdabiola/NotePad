package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.model.NoteDrawing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestNoteDrawingRepository : NoteDrawingRepository {
    private val drawings = mutableListOf<NoteDrawing>()
    private var nextId = 1L

    override suspend fun upserts(drawings: List<NoteDrawing>): List<Long> {
        val ids = mutableListOf<Long>()
        drawings.forEach { drawing ->
            ids.add(upsert(drawing))
        }
        return ids
    }

    override suspend fun upsert(drawing: NoteDrawing): Long {
        return if (drawing.id == -1L) {
            val newDrawing = drawing.copy(id = nextId++)
            drawings.add(newDrawing)
            newDrawing.id
        } else {
            val index = drawings.indexOfFirst { it.id == drawing.id }
            if (index != -1) {
                drawings[index] = drawing
                drawing.id
            } else {
                val newDrawing = drawing.copy(id = nextId++)
                drawings.add(newDrawing)
                newDrawing.id
            }
        }
    }

    override suspend fun delete(id: Long) {
        drawings.removeIf { it.id == id }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        drawings.removeIf { it.noteId == noteId }
    }

    override fun getAll(): Flow<List<NoteDrawing>> {
        return flowOf(drawings.toList())
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteDrawing>> {
        return flowOf(drawings.filter { it.noteId == noteId }.toList())
    }

    override fun get(id: Long): Flow<NoteDrawing?> {
        return flowOf(drawings.find { it.id == id })
    }
}
