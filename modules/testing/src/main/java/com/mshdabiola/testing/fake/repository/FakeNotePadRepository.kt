package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import javax.inject.Inject

class FakeNotePadRepository @Inject constructor() : INotePadRepository {
    override suspend fun upsert(notePad: NotePad): Long {
        return 1
    }

    override suspend fun upsert(notePads: List<NotePad>) {
    }

    override suspend fun deleteCheckNote(id: Long, noteId: Long) {
    }

    override suspend fun deleteNoteCheckByNoteId(noteId: Long) {
    }

    override fun getNotePads(noteType: NoteType): Flow<List<NotePad>> {
        return flow { emptyList<NotePad>() }
    }

    override fun getNotePads(): Flow<List<NotePad>> {
        return flow { emptyList<NotePad>() }
    }

    override fun getOneNotePad(id: Long): Flow<NotePad?> {
        return flow { NotePad() }
    }

    override suspend fun deleteTrashType() {
    }

    override suspend fun deleteNotePad(notePads: List<NotePad>) {
    }

    override suspend fun delete(notePads: List<NotePad>) {
    }

    override fun timeToString(time: LocalTime): String {
        return ""
    }

    override fun dateToString(date: LocalDate): String {
        return ""
    }

    override fun dateToString(long: Long): String {
        return ""
    }

    override suspend fun deleteVoiceNote(id: Long) {
    }

    override fun saveImage(uri: String): Long {
        return 3
    }

    override fun saveVoice(uri: String): Long {
        return 2
    }

    override fun getUri(): String {
        return ""
    }

    override fun getVoicePath(id: Long): String {
        return ""
    }

    override fun getImagePath(id: Long): String {
        return ""
    }

    override suspend fun deleteImageNote(id: Long) {
    }
}
