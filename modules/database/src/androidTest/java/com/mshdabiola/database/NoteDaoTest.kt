package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var noteDao: NoteDao
    private lateinit var db: NoteDatabase // Assuming your RoomDatabase class is AppDatabase

    // Sample data for testing
    private val note1 = NoteEntity(
        id = 1, title = "Note 1", detail = "Detail 1",
        noteType = NoteType.NOTE,
        editDate = 15,
        isCheck = false,
        color = 5,
        background = 5,
        isPin = false,
    )
    private val note2 = NoteEntity(
        id = 2, title = "Note 2", detail = "Detail 2", noteType = NoteType.NOTE,
        editDate = 15,
        isCheck = false,
        color = 5,
        background = 5,
        isPin = false,
    )
    private val note3 = NoteEntity(
        id = 3, title = "Archived Note", detail = "Archived Detail", noteType = NoteType.ARCHIVE,
        editDate = 15,
        isCheck = false,
        color = 5,
        background = 5,
        isPin = false,
    )
    private val note4 = NoteEntity(
        id = 4, title = "Trashed Note", detail = "Trashed Detail", noteType = NoteType.TRASH,
        editDate = 15,
        isCheck = false,
        color = 5,
        background = 5,
        isPin = false,
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        noteDao = db.getNoteDao() // Assuming your AppDatabase has a noteDao() method
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetNote() = runTest {
        val insertedId = noteDao.upsert(note1)
        assertEquals(note1.id, insertedId)

        val retrievedNotePad = noteDao.get(note1.id ?: 3).first()
        assertNotNull(retrievedNotePad)
        assertEquals(note1.id, retrievedNotePad.noteEntity.id)
        assertEquals(note1.title, retrievedNotePad.noteEntity.title)
    }

    @Test
    @Throws(Exception::class)
    fun upsertUpdatesExistingNote() = runTest {
        noteDao.upsert(note1)
        val updatedNote = note1.copy(title = "Updated Title")
        val updatedId = noteDao.upsert(updatedNote)

        assertEquals(-1, updatedId) // ID should remain the same
        val retrievedNotePad = noteDao.get(note1.id ?: 3).first()
        assertNotNull(retrievedNotePad)
        assertEquals("Updated Title", retrievedNotePad.noteEntity.title)
    }

    @Test
    @Throws(Exception::class)
    fun upsertsAndGetAllNotes() = runTest {
        val notesToInsert = listOf(note1, note2)
        val insertedIds = noteDao.upserts(notesToInsert)
        assertContentEquals(listOf(note1.id, note2.id), insertedIds)

        val allNotePads = noteDao.getAll().first()
        assertEquals(2, allNotePads.size)
        // Verify content (more thorough checks might compare all fields or use containsAll)
        assertTrue(allNotePads.any { it.noteEntity.id == note1.id })
        assertTrue(allNotePads.any { it.noteEntity.id == note2.id })
    }

    @Test
    @Throws(Exception::class)
    fun deleteNote() = runTest {
        noteDao.upsert(note1)
        noteDao.upsert(note2)

        noteDao.delete(note1.id ?: 3)

        assertNull(noteDao.get(note1.id ?: 3).first())
        assertNotNull(noteDao.get(note2.id ?: 3).first())
    }

    @Test
    @Throws(Exception::class)
    fun deleteIds() = runTest {
        noteDao.upserts(listOf(note1, note2, note3))
        val idsToDelete = setOf(note1.id ?: 3, note3.id ?: 3)

        noteDao.deleteIds(idsToDelete)

        assertNull(noteDao.get(note1.id!!).first())
        assertNotNull(noteDao.get(note2.id!!).first())
        assertNull(noteDao.get(note3.id!!).first())
    }

    @Test
    @Throws(Exception::class)
    fun deleteTrashNotes() = runTest {
        noteDao.upserts(listOf(note1, note2, note3, note4)) // note4 is TRASH

        noteDao.deleteTrash(NoteType.TRASH)

        assertNotNull(noteDao.get(note1.id!!).first()) // NORMAL
        assertNotNull(noteDao.get(note3.id!!).first()) // ARCHIVE
        assertNull(noteDao.get(note4.id!!).first()) // TRASH should be deleted
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteTypeNormal() = runTest {
        noteDao.upserts(listOf(note1, note2, note3, note4))

        val normalNotes = noteDao.getByNoteType(NoteType.NOTE).first()
        assertEquals(2, normalNotes.size)
        assertTrue(normalNotes.all { it.noteEntity.noteType == NoteType.NOTE })
        assertTrue(normalNotes.any { it.noteEntity.id == note1.id })
        assertTrue(normalNotes.any { it.noteEntity.id == note2.id })
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteTypeArchive() = runTest {
        noteDao.upserts(listOf(note1, note2, note3, note4))

        val archivedNotes = noteDao.getByNoteType(NoteType.ARCHIVE).first()
        assertEquals(1, archivedNotes.size)
        assertEquals(note3.id, archivedNotes.first().noteEntity.id)
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteTypeTrashWhenEmpty() = runTest {
        noteDao.upserts(listOf(note1, note3)) // No trash notes

        val trashNotes = noteDao.getByNoteType(NoteType.TRASH).first()
        assertTrue(trashNotes.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenEmptyReturnsEmptyList() = runTest {
        val allNotes = noteDao.getAll().first()
        assertTrue(allNotes.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getByIds() = runTest {
        noteDao.upserts(listOf(note1, note2, note3, note4))
        val idsToFetch = setOf(note1.id!!, note3.id!!, 99L) // 99L is a non-existent ID

        val fetchedNotePads = noteDao.getByIds(idsToFetch).first()
        assertEquals(2, fetchedNotePads.size) // Should only return existing notes
        assertTrue(fetchedNotePads.any { it.noteEntity.id == note1.id })
        assertTrue(fetchedNotePads.any { it.noteEntity.id == note3.id })
        assertTrue(fetchedNotePads.none { it.noteEntity.id == 99L })
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistentNoteReturnsNull() = runTest {
        val retrievedNote = noteDao.get(999L).first()
        assertNull(retrievedNote)
    }
}
