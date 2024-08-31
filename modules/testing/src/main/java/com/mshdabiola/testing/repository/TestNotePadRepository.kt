package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow

internal class TestNotePadRepository : INotePadRepository {
    override suspend fun insertNote(note: Note): Long {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(notePad: NotePad): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCheckNote(id: Long, noteId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNoteCheckByNoteId(noteId: Long) {
        TODO("Not yet implemented")
    }

    override fun getNotePads(noteType: NoteType): Flow<List<NotePad>> {
        TODO("Not yet implemented")
    }

    override fun getNotePads(): Flow<List<NotePad>> {
        TODO("Not yet implemented")
    }

    override fun getOneNotePad(id: Long): Flow<NotePad?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrashType() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNotePad(notePads: List<NotePad>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(notePads: List<NotePad>) {
        TODO("Not yet implemented")
    }
}
