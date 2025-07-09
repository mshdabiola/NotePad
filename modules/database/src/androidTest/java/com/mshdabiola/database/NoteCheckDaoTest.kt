package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.model.NoteCheckEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.model.NoteType // Assuming you need this for NoteEntity
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
class NoteCheckDaoTest {
    private lateinit var noteCheckDao: NoteCheckDao
    private lateinit var noteDao: NoteDao // To insert a parent NoteEntity first
    private lateinit var db: NoteDatabase // Assuming your RoomDatabase class is AppDatabase

    private val parentNoteId = 1L
    private val parentNote = NoteEntity(
        id = parentNoteId, title = "Parent Note", detail = "Checklist parent", noteType = NoteType.NOTE,
        editDate = 333,
        isCheck = false,
        color = 3,
        background = 3,
        isPin = false,
    )

    private val check1 = NoteCheckEntity(id = 10L, noteId = parentNoteId, content = "Item 1", isCheck = false)
    private val check2 = NoteCheckEntity(id = 11L, noteId = parentNoteId, content = "Item 2", isCheck = true)
    private val check3 = NoteCheckEntity(id = 12L, noteId = parentNoteId, content = "Item 3", isCheck = false)
    private val checkOtherNote = NoteCheckEntity(id = 13L, noteId = 2L, content = "Other note item", isCheck = false) // For a different note

    @Before
    fun createDb() = runTest { // Make @Before suspending to insert parent note
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        )
            .allowMainThreadQueries() // For simplicity in tests
            .build()
        noteCheckDao = db.getNoteCheckDao()
        noteDao = db.getNoteDao()

        // Insert a parent NoteEntity because NoteCheckEntity likely has a foreign key to NoteEntity
        noteDao.upsert(parentNote)
        noteDao.upsert(
            NoteEntity(
                id = 2L, title = "Another Parent", noteType = NoteType.NOTE,
                detail = "detail",
                editDate = 333,
                isCheck = false,
                color = 3,
                background = 3,
                isPin = false,
            ),
        ) // For checkOtherNote
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetCheck() = runTest {
        val insertedId = noteCheckDao.upsert(check1)
        assertEquals(check1.id, insertedId)

        val retrievedCheck = noteCheckDao.get(check1.id!!).first()
        assertNotNull(retrievedCheck)
        assertEquals(check1.id, retrievedCheck.id)
        assertEquals(check1.content, retrievedCheck.content)
        assertEquals(check1.isCheck, retrievedCheck.isCheck)
        assertEquals(check1.noteId, retrievedCheck.noteId)
    }

    @Test
    @Throws(Exception::class)
    fun upsertUpdatesExistingCheck() = runTest {
        noteCheckDao.upsert(check1)
        val updatedCheck = check1.copy(content = "Updated Item 1", isCheck = true)
        val updatedId = noteCheckDao.upsert(updatedCheck)

        assertEquals(check1.id, updatedId) // ID should remain the same
        val retrievedCheck = noteCheckDao.get(check1.id!!).first()
        assertNotNull(retrievedCheck)
        assertEquals("Updated Item 1", retrievedCheck.content)
        assertTrue(retrievedCheck.isCheck)
    }

    @Test
    @Throws(Exception::class)
    fun upsertsAndGetByNoteId() = runTest {
        val checksForParentNote = listOf(check1, check2)
        val insertedIds = noteCheckDao.upserts(checksForParentNote)
        assertContentEquals(listOf(check1.id, check2.id), insertedIds)

        // Insert a check for another note to ensure getByNoteId filters correctly
        noteCheckDao.upsert(checkOtherNote)

        val retrievedChecks = noteCheckDao.getByNoteId(parentNoteId).first()
        assertEquals(2, retrievedChecks.size)
        assertTrue(retrievedChecks.any { it.id == check1.id })
        assertTrue(retrievedChecks.any { it.id == check2.id })
        assertTrue(retrievedChecks.none { it.id == checkOtherNote.id }) // Ensure it's not fetching checks from other notes
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenMultipleNotes() = runTest {
        noteCheckDao.upserts(listOf(check1, check2, checkOtherNote))

        val allChecks = noteCheckDao.getAll().first()
        assertEquals(3, allChecks.size)
        assertTrue(allChecks.any { it.id == check1.id })
        assertTrue(allChecks.any { it.id == check2.id })
        assertTrue(allChecks.any { it.id == checkOtherNote.id })
    }

    @Test
    @Throws(Exception::class)
    fun deleteCheck() = runTest {
        noteCheckDao.upsert(check1)
        noteCheckDao.upsert(check2)

        noteCheckDao.delete(check1.id!!)

        assertNull(noteCheckDao.get(check1.id!!).first())
        assertNotNull(noteCheckDao.get(check2.id!!).first())
    }

    @Test
    @Throws(Exception::class)
    fun deleteCheckedItemsForNote() = runTest {
        // check1 (isCheck=false), check2 (isCheck=true), check3 (isCheck=false)
        noteCheckDao.upserts(listOf(check1, check2, check3))
        // Add a checked item for another note to ensure it's not deleted
        val otherNoteCheckedItem = NoteCheckEntity(id = 100L, noteId = 2L, content = "Checked other", isCheck = true)
        noteCheckDao.upsert(otherNoteCheckedItem)

        noteCheckDao.deleteCheckedItems(parentNoteId)

        assertNotNull(noteCheckDao.get(check1.id!!).first()) // Should remain
        assertNull(noteCheckDao.get(check2.id!!).first()) // Should be deleted
        assertNotNull(noteCheckDao.get(check3.id!!).first()) // Should remain
        assertNotNull(noteCheckDao.get(otherNoteCheckedItem.id!!).first()) // Checked item from other note should remain
    }

    @Test
    @Throws(Exception::class)
    fun deleteByNoteId() = runTest {
        noteCheckDao.upserts(listOf(check1, check2, check3))
        noteCheckDao.upsert(checkOtherNote) // Belongs to noteId = 2L

        noteCheckDao.deleteByNoteId(parentNoteId)

        assertNull(noteCheckDao.get(check1.id!!).first())
        assertNull(noteCheckDao.get(check2.id!!).first())
        assertNull(noteCheckDao.get(check3.id!!).first())
        assertNotNull(noteCheckDao.get(checkOtherNote.id!!).first()) // Should remain
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistentCheckReturnsNull() = runTest {
        val retrievedCheck = noteCheckDao.get(999L).first()
        assertNull(retrievedCheck)
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenEmptyReturnsEmptyList() = runTest {
        // Ensure no parent note items are present that could interfere if deleteByNoteId wasn't perfect
        noteCheckDao.deleteByNoteId(parentNoteId)
        noteCheckDao.deleteByNoteId(2L)

        val allChecks = noteCheckDao.getAll().first()
        assertTrue(allChecks.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteIdWhenEmptyReturnsEmptyList() = runTest {
        val checks = noteCheckDao.getByNoteId(parentNoteId).first()
        assertTrue(checks.isEmpty())
    }
}
