package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal class TestNotePadRepository : INotePadRepository {
    override suspend fun upsert(notePad: NotePad): Long {
        TODO("Not yet implemented")
    }

    override suspend fun upsert(notePads: List<NotePad>) {
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

    override fun timeToString(time: LocalTime): String {
        TODO("Not yet implemented")
    }

    override fun dateToString(date: LocalDate): String {
        TODO("Not yet implemented")
    }

    override fun dateToString(long: Long): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVoiceNote(id: Long) {
        TODO("Not yet implemented")
    }

    override fun saveImage(uri: String): Long {
        TODO("Not yet implemented")
    }

    override fun saveVoice(uri: String): Long {
        TODO("Not yet implemented")
    }

    override fun getUri(): String {
        TODO("Not yet implemented")
    }

    override fun getVoicePath(id: Long): String {
        TODO("Not yet implemented")
    }

    override fun getImagePath(id: Long): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteImageNote(id: Long) {
        TODO("Not yet implemented")
    }
}
