package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteLabelEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteLabelDaoTest {

    private lateinit var noteLabelDao: NoteLabelDao
    private lateinit var noteDao: NoteDao
    private lateinit var labelDao: LabelDao
    private lateinit var db: NoteDatabase

    // Sample IDs for prerequisite entities
    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L
    private var testLabelId1: Long = -1L
    private var testLabelId2: Long = -1L
    private var testLabelId3: Long = -1L

    @Before
    fun createDb() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        ).build()

        noteLabelDao = db.getNoteLabelDao() // Ensure this method exists in NoteDatabase
        noteDao = db.getNoteDao() // Ensure this method exists in NoteDatabase
        labelDao = db.getLabelDao() // Ensure this method exists in NoteDatabase

        // Insert dummy notes
        testNoteId1 = noteDao.upsert(
            NoteEntity(
                id = null, title = "Note 1",
                detail = "Detail for Test Note 1",
                editDate = 533,
                isCheck = false,
                color = 4, // Example color
                background = 3,
                isPin = false,
                noteType = NoteType.NOTE,
            ),
        )
        testNoteId2 = noteDao.upsert(
            NoteEntity(
                id = null, title = "Note 2", detail = "Detail for Test Note 1",
                editDate = 5336,
                isCheck = false,
                color = 4, // Example color
                background = 3,
                isPin = false,
                noteType = NoteType.NOTE,
            ),
        )

        // Insert dummy labels
        testLabelId1 = labelDao.upsert(LabelEntity(id = null, name = "Label 1"))
        testLabelId2 = labelDao.upsert(LabelEntity(id = null, name = "Label 2"))
        testLabelId3 = labelDao.upsert(LabelEntity(id = null, name = "Label 3"))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetByNoteId() = runTest {
        val noteLabel1 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1)
        val noteLabel2 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2)

        val insertedId1 = noteLabelDao.upsert(noteLabel1) // Should be > 0 if Room considers it new
        val insertedId2 = noteLabelDao.upsert(noteLabel2)

        // For composite PKs, Room's upsert returning Long might return the rowId.
        // The important part is that the data is there.
        assertTrue(insertedId1 > 0)
        assertTrue(insertedId2 > 0)

        val retrievedNoteLabels = noteLabelDao.getByNoteId(testNoteId1).first()
        assertEquals(2, retrievedNoteLabels.size)
        assertTrue(retrievedNoteLabels.any { it.labelId == testLabelId1 && it.noteId == testNoteId1 })
        assertTrue(retrievedNoteLabels.any { it.labelId == testLabelId2 && it.noteId == testNoteId1 })
    }

    @Test
    @Throws(Exception::class)
    fun upsertsAndGetAll() = runTest {
        val noteLabels = listOf(
            NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1),
            NoteLabelEntity(noteId = testNoteId2, labelId = testLabelId2),
            NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId3),
        )
        val insertedIds = noteLabelDao.upserts(noteLabels)

        assertEquals(3, insertedIds.size)
        assertTrue(insertedIds.all { it > 0 }) // All returned rowIds should be positive

        val allNoteLabels = noteLabelDao.getAll().first()
        assertEquals(3, allNoteLabels.size)
        // Check if all inserted items are present
        noteLabels.forEach { expected ->
            assertTrue(
                allNoteLabels.any { actual ->
                    actual.noteId == expected.noteId && actual.labelId == expected.labelId
                },
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun deleteByNoteId() = runTest {
        val nl1_1 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1) // For note 1
        val nl1_2 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2) // For note 1
        val nl2_1 = NoteLabelEntity(noteId = testNoteId2, labelId = testLabelId1) // For note 2

        noteLabelDao.upsert(nl1_1)
        noteLabelDao.upsert(nl1_2)
        noteLabelDao.upsert(nl2_1)

        noteLabelDao.deleteByNoteId(testNoteId1)

        val note1Labels = noteLabelDao.getByNoteId(testNoteId1).first()
        assertTrue(note1Labels.isEmpty())

        val note2Labels = noteLabelDao.getByNoteId(testNoteId2).first()
        assertEquals(1, note2Labels.size)
        assertEquals(testLabelId1, note2Labels.first().labelId)
    }

    @Test
    @Throws(Exception::class)
    fun deleteByNoteIdAndLabelId() = runTest {
        val nl1 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1)
        val nl2 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2)
        noteLabelDao.upsert(nl1)
        noteLabelDao.upsert(nl2)

        noteLabelDao.deleteByNoteIdAndLabelId(testNoteId1, testLabelId1)

        val note1Labels = noteLabelDao.getByNoteId(testNoteId1).first()
        assertEquals(1, note1Labels.size)
        assertEquals(testLabelId2, note1Labels.first().labelId)
    }

    @Test
    @Throws(Exception::class)
    fun getByLabelId() = runTest {
        val nl1 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1)
        val nl2 = NoteLabelEntity(noteId = testNoteId2, labelId = testLabelId1)
        val nl3 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2)

        noteLabelDao.upsert(nl1)
        noteLabelDao.upsert(nl2)
        noteLabelDao.upsert(nl3)

        val labelsForLabel1 = noteLabelDao.getByLabelId(testLabelId1).first()
        assertEquals(2, labelsForLabel1.size)
        assertTrue(labelsForLabel1.any { it.noteId == testNoteId1 })
        assertTrue(labelsForLabel1.any { it.noteId == testNoteId2 })

        val labelsForLabel2 = noteLabelDao.getByLabelId(testLabelId2).first()
        assertEquals(1, labelsForLabel2.size)
        assertEquals(testNoteId1, labelsForLabel2.first().noteId)
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteIds() = runTest {
        val nl_n1_l1 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1)
        val nl_n1_l2 = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2)
        // Insert a third note and label for more comprehensive testing if needed
        val tempNoteId3 = noteDao.upsert(
            NoteEntity(
                id = null, title = "Temp Note 3",
                detail = "Detail for Test Note 1",
                editDate = 65,
                isCheck = false,
                color = 4, // Example color
                background = 3,
                isPin = false,
                noteType = NoteType.NOTE,
            ),
        )
        val nl_n3_l1 = NoteLabelEntity(noteId = tempNoteId3, labelId = testLabelId1)

        noteLabelDao.upserts(listOf(nl_n1_l1, nl_n1_l2, nl_n3_l1))

        val results = noteLabelDao.getByNoteIds(setOf(testNoteId1, testNoteId2)).first() // testNoteId2 has no labels yet

        assertEquals(2, results.size) // Should only get labels for testNoteId1
        assertTrue(results.any { it.noteId == testNoteId1 && it.labelId == testLabelId1 })
        assertTrue(results.any { it.noteId == testNoteId1 && it.labelId == testLabelId2 })
        assertFalse(results.any { it.noteId == tempNoteId3 }) // Ensure it's not fetching for other notes

        val resultsForNote1And3 = noteLabelDao.getByNoteIds(setOf(testNoteId1, tempNoteId3)).first()
        assertEquals(3, resultsForNote1And3.size)
        assertTrue(resultsForNote1And3.any { it.noteId == testNoteId1 && it.labelId == testLabelId1 })
        assertTrue(resultsForNote1And3.any { it.noteId == testNoteId1 && it.labelId == testLabelId2 })
        assertTrue(resultsForNote1And3.any { it.noteId == tempNoteId3 && it.labelId == testLabelId1 })
    }

    @Test
    @Throws(Exception::class)
    fun upsertDoesNotDuplicate() = runTest {
        val noteLabel = NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1)
        noteLabelDao.upsert(noteLabel)
        noteLabelDao.upsert(noteLabel) // Try to upsert the exact same entity

        val retrievedNoteLabels = noteLabelDao.getByNoteId(testNoteId1).first()
        assertEquals(1, retrievedNoteLabels.size) // Should still be 1 due to composite PK
        assertEquals(testLabelId1, retrievedNoteLabels.first().labelId)
    }

    @Test
    @Throws(Exception::class)
    fun cascadeDeleteWhenNoteIsDeleted() = runTest {
        noteLabelDao.upsert(NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2))
        noteLabelDao.upsert(NoteLabelEntity(noteId = testNoteId2, labelId = testLabelId1)) // Different note, same label

        // Ensure they exist
        assertEquals(2, noteLabelDao.getByNoteId(testNoteId1).first().size)
        assertEquals(1, noteLabelDao.getByNoteId(testNoteId2).first().size)

        // Delete testNoteId1
        noteDao.delete(testNoteId1) // Assuming NoteDao has delete(id: Long)

        val labelsForNote1 = noteLabelDao.getByNoteId(testNoteId1).first()
        assertTrue(labelsForNote1.isEmpty()) // Should be gone due to cascade

        val labelsForNote2 = noteLabelDao.getByNoteId(testNoteId2).first()
        assertEquals(1, labelsForNote2.size) // Should still exist
    }

    @Test
    @Throws(Exception::class)
    fun cascadeDeleteWhenLabelIsDeleted() = runTest {
        noteLabelDao.upsert(NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId1))
        noteLabelDao.upsert(NoteLabelEntity(noteId = testNoteId2, labelId = testLabelId1)) // Different note, same label
        noteLabelDao.upsert(NoteLabelEntity(noteId = testNoteId1, labelId = testLabelId2)) // Same note, different label

        // Ensure they exist
        assertEquals(2, noteLabelDao.getByLabelId(testLabelId1).first().size)
        assertEquals(1, noteLabelDao.getByLabelId(testLabelId2).first().size)

        // Delete testLabelId1
        labelDao.delete(testLabelId1) // Assuming LabelDao has delete(id: Long)

        val notesForLabel1 = noteLabelDao.getByLabelId(testLabelId1).first()
        assertTrue(notesForLabel1.isEmpty()) // Should be gone due to cascade

        val notesForLabel2 = noteLabelDao.getByLabelId(testLabelId2).first()
        assertEquals(1, notesForLabel2.size) // Should still exist
        assertEquals(testNoteId1, notesForLabel2.first().noteId)
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenEmptyReturnsEmptyList() = runTest {
        // Ensure table is empty
        noteLabelDao.deleteByNoteId(testNoteId1)
        noteLabelDao.deleteByNoteId(testNoteId2)
        // also clear any by labels if some notes were deleted before labels
        val allLabels = labelDao.getAll().first()
        allLabels.forEach { labelDao.delete(it.id!!) }

        val allNoteLabels = noteLabelDao.getAll().first()
        assertTrue(allNoteLabels.isEmpty())
    }
}
