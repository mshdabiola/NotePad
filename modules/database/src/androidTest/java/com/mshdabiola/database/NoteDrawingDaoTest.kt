package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.model.NoteDrawingEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NoteDrawingDaoTest {

    private lateinit var noteDrawingDao: NoteDrawingDao
    private lateinit var noteDao: NoteDao // For creating a prerequisite Note
    private lateinit var db: NoteDatabase

    // Define a sample noteId that will be used for foreign key constraints
    private var testNoteId: Long = -1L

    @Before
    fun createDb() = runTest { // Make Before suspend or runTest if creating note
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        noteDrawingDao = db.getNoteDrawingDao()
        noteDao = db.getNoteDao() // Initialize NoteDao

        // Insert a dummy note to satisfy foreign key constraints
        // Adjust NoteEntity constructor as per your definition
        val dummyNote = NoteEntity(
            id = null, title = "Test Note",
            detail = "Test Detail",
            editDate = System.currentTimeMillis(),
            isCheck = false,
            color = 0,
            background = 0,
            isPin = false,
            noteType = NoteType.NOTE
        )
        testNoteId = noteDao.upsert(dummyNote) // Assuming upsert returns the ID
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetDrawing() = runTest {
        val drawing = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "path1;path2")
        val insertedId = noteDrawingDao.upsert(drawing)

        assertTrue(insertedId > 0) // Check if ID is auto-generated and positive

        val retrievedDrawing = noteDrawingDao.get(insertedId).first()
        assertNotNull(retrievedDrawing)
        assertEquals(insertedId, retrievedDrawing.id)
        assertEquals(testNoteId, retrievedDrawing.noteId)
        assertEquals("path1;path2", retrievedDrawing.paths)
    }

    @Test
    @Throws(Exception::class)
    fun upsertListAndGetAllDrawings() = runTest {
        val drawings = listOf(
            NoteDrawingEntity(id = null, noteId = testNoteId, paths = "pathA"),
            NoteDrawingEntity(id = null, noteId = testNoteId, paths = "pathB"),
        )
        val insertedIds = noteDrawingDao.upserts(drawings)

        assertEquals(2, insertedIds.size)
        assertTrue(insertedIds.all { it > 0 }) // All IDs should be positive

        val allDrawings = noteDrawingDao.getAll().first()
        assertEquals(2, allDrawings.size)
        // Check if the retrieved drawings match the inserted ones (ignoring IDs for this check)
        assertEquals(
            drawings.map { it.copy(id = null) }.toSet(), // Compare based on other properties
            allDrawings.map { it.copy(id = null) }.toSet()
        )
    }

    @Test
    @Throws(Exception::class)
    fun deleteDrawing() = runTest {
        val drawing1 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "toDelete")
        val drawing2 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "toKeep")
        val id1 = noteDrawingDao.upsert(drawing1)
        val id2 = noteDrawingDao.upsert(drawing2)

        noteDrawingDao.delete(id1)

        val retrievedDrawing = noteDrawingDao.get(id1).first()
        assertNull(retrievedDrawing)

        val keptDrawing = noteDrawingDao.get(id2).first()
        assertNotNull(keptDrawing)
    }

    @Test
    @Throws(Exception::class)
    fun deleteByNoteId() = runTest {
        // Insert a second note for isolation
        val anotherNote = NoteEntity(
            id = null, title = "Another Note",
            detail = "Another Detail",
            editDate = System.currentTimeMillis(),
            isCheck = false,
            color = 1,
            background = 1,
            isPin = false, noteType = NoteType.NOTE
        )
        val anotherNoteId = noteDao.upsert(anotherNote)

        val drawing1 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "path1")
        val drawing2 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "path2")
        val drawingForAnotherNote = NoteDrawingEntity(id = null, noteId = anotherNoteId, paths = "path3")

        noteDrawingDao.upsert(drawing1)
        noteDrawingDao.upsert(drawing2)
        val id3 = noteDrawingDao.upsert(drawingForAnotherNote)


        noteDrawingDao.deleteByNoteId(testNoteId)

        val drawingsForTestNote = noteDrawingDao.getByNoteId(testNoteId).first()
        assertTrue(drawingsForTestNote.isEmpty())

        val drawingForOtherNoteRetrieved = noteDrawingDao.get(id3).first()
        assertNotNull(drawingForOtherNoteRetrieved) // Should still exist
    }


    @Test
    @Throws(Exception::class)
    fun getNonExistentDrawingReturnsNull() = runTest {
        val retrievedDrawing = noteDrawingDao.get(999L).first() // Use a non-existent ID
        assertNull(retrievedDrawing)
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenEmptyReturnsEmptyList() = runTest {
        // Ensure no drawings are present (deleteByNoteId might be needed if @Before added some)
        noteDrawingDao.deleteByNoteId(testNoteId)
        val allDrawings = noteDrawingDao.getAll().first()
        assertTrue(allDrawings.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteId() = runTest {
        // Insert a second note for isolation
        val anotherNote = NoteEntity(
            id = null, title = "Another Note",
            detail = "Another Detail",
            editDate = System.currentTimeMillis(),
            isCheck = false,
            color = 1,
            background = 1,
            isPin = false, noteType = NoteType.NOTE
        )
        val anotherNoteId = noteDao.upsert(anotherNote)


        val drawing1 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "path1_note1")
        val drawing2 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "path2_note1")
        val drawingForAnotherNote = NoteDrawingEntity(id = null, noteId = anotherNoteId, paths = "path1_note2")

        val id1 = noteDrawingDao.upsert(drawing1)
        val id2 = noteDrawingDao.upsert(drawing2)
        noteDrawingDao.upsert(drawingForAnotherNote)

        val drawingsForTestNote = noteDrawingDao.getByNoteId(testNoteId).first()
        assertEquals(2, drawingsForTestNote.size)
        assertTrue(drawingsForTestNote.any { it.id == id1 })
        assertTrue(drawingsForTestNote.any { it.id == id2 })

        val drawingsForAnotherNote = noteDrawingDao.getByNoteId(anotherNoteId).first()
        assertEquals(1, drawingsForAnotherNote.size)
        assertEquals("path1_note2", drawingsForAnotherNote.first().paths)
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteIdWhenNoDrawingsReturnsEmptyList() = runTest {
        val drawings = noteDrawingDao.getByNoteId(testNoteId).first() // testNoteId has no drawings yet
        assertTrue(drawings.isEmpty())

        // Insert a drawing for a different note
        val anotherNote = NoteEntity(
            id = null, title = "Another Note",
            detail = "Detail for different note",
            editDate = System.currentTimeMillis(),
            isCheck = true,
            color = 2,
            background = 2,
            isPin = true, noteType = NoteType.NOTE
        )
        val anotherNoteId = noteDao.upsert(anotherNote)
        noteDrawingDao.upsert(NoteDrawingEntity(id = null, noteId = anotherNoteId, paths = "some_path"))

        val drawingsForTestNoteAgain = noteDrawingDao.getByNoteId(testNoteId).first()
        assertTrue(drawingsForTestNoteAgain.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun upsertUpdatesExistingDrawing() = runTest {
        val initialDrawing = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "old_path")
        val insertedId = noteDrawingDao.upsert(initialDrawing)

        val updatedDrawing = NoteDrawingEntity(id = insertedId, noteId = testNoteId, paths = "new_path")
        val updatedIdResult = noteDrawingDao.upsert(updatedDrawing)
        // Room's @Upsert with an existing ID that matches will perform an update.
        // The returned value for an update in an upsert that returns Long (single item) is the rowId of the inserted/updated item.
        // If the ID is auto-generated and you provide it, it's an update.
        assertEquals(insertedId, updatedIdResult)


        val retrievedDrawing = noteDrawingDao.get(insertedId).first()
        assertNotNull(retrievedDrawing)
        assertEquals("new_path", retrievedDrawing.paths)
        assertEquals(testNoteId, retrievedDrawing.noteId) // Ensure noteId isn't accidentally changed
    }

    @Test
    @Throws(Exception::class)
    fun upsertsUpdatesExistingDrawingsAndInsertsNewOnes() = runTest {
        val initialDrawing1 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "old_drawing1")
        val initialDrawing2 = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "old_drawing2")

        // Manually insert and get IDs as they are auto-generated
        val id1 = noteDrawingDao.upsert(initialDrawing1)
        val id2 = noteDrawingDao.upsert(initialDrawing2)

        val updatedDrawingsToUpsert = listOf(
            NoteDrawingEntity(id = id1, noteId = testNoteId, paths = "new_drawing1"), // Update
            NoteDrawingEntity(id = null, noteId = testNoteId, paths = "new_drawing3"),   // Insert
        )
        val resultIds = noteDrawingDao.upserts(updatedDrawingsToUpsert)

        // For upserts(List<T>): Long the returned list contains the rowIds of the inserted/updated items.
        // If an item was updated, its original rowId (which is its PK if it's the PK) will be in the list.
        // If an item was inserted, its new auto-generated rowId will be in the list.
        assertEquals(2, resultIds.size)
        assertTrue(resultIds.contains(id1)) // id1 should be present as it was updated
        val id3 = resultIds.first { it != id1 } // The other ID is the new one
        assertTrue(id3 > 0 && id3 != id1 && id3 != id2)


        val drawing1Retrieved = noteDrawingDao.get(id1).first()
        assertNotNull(drawing1Retrieved)
        assertEquals("new_drawing1", drawing1Retrieved.paths)

        val drawing2Retrieved = noteDrawingDao.get(id2).first() // Should still exist and be unchanged
        assertNotNull(drawing2Retrieved)
        assertEquals("old_drawing2", drawing2Retrieved.paths)

        val drawing3Retrieved = noteDrawingDao.get(id3).first()
        assertNotNull(drawing3Retrieved)
        assertEquals("new_drawing3", drawing3Retrieved.paths)

        val allDrawings = noteDrawingDao.getAll().first()
        assertEquals(3, allDrawings.size) // id1 (updated), id2 (original), id3 (new)
    }

    @Test
    @Throws(Exception::class)
    fun foreignKeyConstraintOnDeleteNoteCascade() = runTest {
        val drawingForCascadeTest = NoteDrawingEntity(id = null, noteId = testNoteId, paths = "cascade_delete_test")
        val drawingId = noteDrawingDao.upsert(drawingForCascadeTest)

        // Ensure drawing exists
        assertNotNull(noteDrawingDao.get(drawingId).first())

        // Delete the parent note
        noteDao.delete(testNoteId) // Assuming NoteDao has a delete(id: Long) method

        // The drawing should now be deleted due to CASCADE rule
        assertNull(noteDrawingDao.get(drawingId).firstOrNull())
    }
}
