package com.mshdabiola.database

// Remove the Google Truth import:
// import com.google.common.truth.Truth.assertThat
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.model.LabelEntity
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
class LabelDaoTest {

    private lateinit var labelDao: LabelDao
    private lateinit var db: NoteDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        )
            .allowMainThreadQueries() // Allowing main thread queries for simplicity in tests
            .build()
        labelDao = db.getLabelDao() // Assuming your AppDatabase has a labelDao() method
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetLabel() = runTest {
        val label = LabelEntity(id = 1, name = "Work")
        val insertedId = labelDao.upsert(label)

        assertEquals(1L, insertedId)

        val retrievedLabel = labelDao.get(1L).first()
        assertNotNull(retrievedLabel)
        assertEquals(label.id, retrievedLabel.id)
        assertEquals(label.name, retrievedLabel.name)
    }

    @Test
    @Throws(Exception::class)
    fun upsertListAndGetAllLabels() = runTest {
        val labels = listOf(
            LabelEntity(id = 1, name = "Work"),
            LabelEntity(id = 2, name = "Personal"),
        )
        val insertedIds = labelDao.upserts(labels)

        assertEquals(2, insertedIds.size)
        assertContentEquals(listOf(1L, 2L), insertedIds) // Checks content and order

        val allLabels = labelDao.getAll().first()
        assertEquals(2, allLabels.size)
        // For lists of complex objects, you might need to iterate or ensure your data class implements equals correctly
        // assertContentEquals is good for order and content if equals is well-defined.
        // Alternatively, check properties:
        assertTrue(allLabels.containsAll(labels) && labels.containsAll(allLabels))
    }

    @Test
    @Throws(Exception::class)
    fun deleteLabel() = runTest {
        val label1 = LabelEntity(id = 1, name = "To Delete")
        val label2 = LabelEntity(id = 2, name = "To Keep")
        labelDao.upsert(label1)
        labelDao.upsert(label2)

        labelDao.delete(1L)

        val retrievedLabel = labelDao.get(1L).first()
        assertNull(retrievedLabel)

        val keptLabel = labelDao.get(2L).first()
        assertNotNull(keptLabel)
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistentLabelReturnsNull() = runTest {
        val retrievedLabel = labelDao.get(99L).first()
        assertNull(retrievedLabel)
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenEmptyReturnsEmptyList() = runTest {
        val allLabels = labelDao.getAll().first()
        assertTrue(allLabels.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun upsertUpdatesExistingLabel() = runTest {
        val initialLabel = LabelEntity(id = 1, name = "Old Name")
        labelDao.upsert(initialLabel)

        val updatedLabel = LabelEntity(id = 1, name = "New Name")
        val updatedId = labelDao.upsert(updatedLabel)
        assertEquals(-1L, updatedId) // Should be the same ID

        val retrievedLabel = labelDao.get(1L).first()
        assertNotNull(retrievedLabel)
        assertEquals("New Name", retrievedLabel.name)
    }

    @Test
    @Throws(Exception::class)
    fun upsertsUpdatesExistingLabelsAndInsertsNewOnes() = runTest {
        val initialLabel1 = LabelEntity(id = 1, name = "Old Label 1")
        val initialLabel2 = LabelEntity(id = 2, name = "Old Label 2")
        labelDao.upserts(listOf(initialLabel1, initialLabel2))

        val updatedLabelsToUpsert = listOf(
            LabelEntity(id = 1, name = "New Label 1"), // Update
            LabelEntity(id = 3, name = "New Label 3"), // Insert
        )
        val resultIds = labelDao.upserts(updatedLabelsToUpsert)
        assertContentEquals(listOf(-1L, 3L), resultIds)

        val label1 = labelDao.get(1L).first()
        assertNotNull(label1)
        assertEquals("New Label 1", label1.name)

        val label2 = labelDao.get(2L).first() // Should still exist
        assertNotNull(label2)
        assertEquals("Old Label 2", label2.name)

        val label3 = labelDao.get(3L).first()
        assertNotNull(label3)
        assertEquals("New Label 3", label3.name)

        val allLabels = labelDao.getAll().first()
        assertEquals(3, allLabels.size)
    }
}
