package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteDao // Assuming you have this
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.model.NoteEntity // Assuming you have this
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking // For simple tests, can also use runTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteImageDaoTest {

    private lateinit var noteImageDao: NoteImageDao
    private lateinit var noteDao: NoteDao // For creating a prerequisite Note
    private lateinit var db: NoteDatabase

    // Define sample noteIds that will be used for foreign key constraints
    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L // For testing with multiple notes

    // Define sample image IDs
    private val imageId1: Long = 1L
    private val imageId2: Long = 2L
    private val imageId3: Long = 3L

    @Before
    fun createDb() = runTest { // Use runTest for suspending operations in @Before
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java,
        )
            //.allowMainThreadQueries() // Not generally recommended for production, but can be fine for tests
            .build()
        noteImageDao = db.getNoteImageDao() // Ensure this method exists in NoteDatabase
        noteDao = db.getNoteDao()         // Ensure this method exists in NoteDatabase

        // Insert dummy notes to satisfy foreign key constraints
        val now = 44444L
        val dummyNote1 = NoteEntity(
            id = null,
            title = "Test Note 1",
            detail = "Detail for Test Note 1",
            editDate = now,
            isCheck = false,
            color = 4, // Example color
            background = 3,
            isPin = false,
            noteType = NoteType.NOTE
        )
        testNoteId1 = noteDao.upsert(dummyNote1) // Assuming upsert returns the ID

        val dummyNote2 = NoteEntity(
            id = null, title = "Test Note 2",
            detail = "Detail for Test Note 2",
            editDate = now,
            isCheck = true,
            color = 4,
            background = 3,
            isPin = true,
            noteType = NoteType.NOTE
        )
        testNoteId2 = noteDao.upsert(dummyNote2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetImage() = runTest {
        val image = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        val insertedId = noteImageDao.upsert(image)

        assertEquals(imageId1, insertedId) // Upsert returns the provided ID for NoteImageEntity

        val retrievedImage = noteImageDao.get(imageId1).first()
        assertNotNull(retrievedImage)
        assertEquals(imageId1, retrievedImage!!.id)
        assertEquals(testNoteId1, retrievedImage.noteId)
    }

    @Test
    @Throws(Exception::class)
    fun upsertsAndGetAllImages() = runTest {
        val images = listOf(
            NoteImageEntity(id = imageId1, noteId = testNoteId1),
            NoteImageEntity(id = imageId2, noteId = testNoteId2),
        )
        val insertedIds = noteImageDao.upserts(images)

        assertEquals(2, insertedIds.size)
        assertTrue(insertedIds.containsAll(listOf(imageId1, imageId2)))

        val allImages = noteImageDao.getAll().first()
        assertEquals(2, allImages.size)
        // Order might not be guaranteed, so check contents
        assertTrue(allImages.any { it.id == imageId1 && it.noteId == testNoteId1 })
        assertTrue(allImages.any { it.id == imageId2 && it.noteId == testNoteId2 })
    }

    @Test
    @Throws(Exception::class)
    fun deleteImage() = runTest {
        val image1 = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        val image2 = NoteImageEntity(id = imageId2, noteId = testNoteId1)
        noteImageDao.upsert(image1)
        noteImageDao.upsert(image2)

        noteImageDao.delete(imageId1)

        val retrievedImage = noteImageDao.get(imageId1).first()
        assertNull(retrievedImage)

        val keptImage = noteImageDao.get(imageId2).first()
        assertNotNull(keptImage)
    }

    @Test
    @Throws(Exception::class)
    fun deleteByNoteId() = runTest {
        val imageForNote1_1 = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        val imageForNote1_2 = NoteImageEntity(id = imageId2, noteId = testNoteId1)
        val imageForNote2 = NoteImageEntity(id = imageId3, noteId = testNoteId2)

        noteImageDao.upsert(imageForNote1_1)
        noteImageDao.upsert(imageForNote1_2)
        noteImageDao.upsert(imageForNote2)

        noteImageDao.deleteByNoteId(testNoteId1)

        val imagesForNote1 = noteImageDao.getByNoteId(testNoteId1).first()
        assertTrue(imagesForNote1.isEmpty())

        val imagesForNote2 = noteImageDao.getByNoteId(testNoteId2).first()
        assertEquals(1, imagesForNote2.size)
        assertEquals(imageId3, imagesForNote2.first().id)
    }

    @Test
    @Throws(Exception::class)
    fun getNonExistentImageReturnsNull() = runTest {
        val retrievedImage = noteImageDao.get(999L).first() // Use a non-existent ID
        assertNull(retrievedImage)
    }

    @Test
    @Throws(Exception::class)
    fun getAllWhenEmptyReturnsEmptyList() = runTest {
        // Ensure table is empty
        noteImageDao.deleteByNoteId(testNoteId1) // Clear any potential leftovers
        noteImageDao.deleteByNoteId(testNoteId2)

        val allImages = noteImageDao.getAll().first()
        assertTrue(allImages.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteId() = runTest {
        val image1_note1 = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        val image2_note1 = NoteImageEntity(id = imageId2, noteId = testNoteId1)
        val image1_note2 = NoteImageEntity(id = imageId3, noteId = testNoteId2)

        noteImageDao.upsert(image1_note1)
        noteImageDao.upsert(image2_note1)
        noteImageDao.upsert(image1_note2)

        val imagesForNote1 = noteImageDao.getByNoteId(testNoteId1).first()
        assertEquals(2, imagesForNote1.size)
        assertTrue(imagesForNote1.any { it.id == imageId1 })
        assertTrue(imagesForNote1.any { it.id == imageId2 })

        val imagesForNote2 = noteImageDao.getByNoteId(testNoteId2).first()
        assertEquals(1, imagesForNote2.size)
        assertEquals(imageId3, imagesForNote2.first().id)
    }

    @Test
    @Throws(Exception::class)
    fun getByNoteIdWhenNoImagesReturnsEmptyList() = runTest {
        // testNoteId1 currently has no images specifically added in this test method yet
        val images = noteImageDao.getByNoteId(testNoteId1).first()
        assertTrue(images.isEmpty())

        // Insert an image for a different note
        noteImageDao.upsert(NoteImageEntity(id = imageId1, noteId = testNoteId2))

        val imagesForTestNote1Again = noteImageDao.getByNoteId(testNoteId1).first()
        assertTrue(imagesForTestNote1Again.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun upsertUpdatesExistingImage() = runTest {
        // Note: For NoteImageEntity, 'upsert' acts as insert or replace because the ID is not auto-generated.
        // If you insert with an ID that already exists, it will replace the old entry.
        // Here, we'll simulate an "update" by inserting a new entity with the same ID but potentially different (though NoteImageEntity only has noteId other than PK).
        // Let's assume we want to re-associate an image ID with a different note (though not typical for this entity design).
        val initialImage = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        noteImageDao.upsert(initialImage)

        // "Update" by inserting with the same ID but different noteId
        val updatedImage = NoteImageEntity(id = imageId1, noteId = testNoteId2)
        val updatedIdResult = noteImageDao.upsert(updatedImage)
        assertEquals(imageId1, updatedIdResult)

        val retrievedImage = noteImageDao.get(imageId1).first()
        assertNotNull(retrievedImage)
        assertEquals(testNoteId2, retrievedImage!!.noteId) // Check if noteId was "updated"
    }

    @Test
    @Throws(Exception::class)
    fun upsertsUpdatesExistingAndInsertsNewImages() = runTest {
        val initialImage1 = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        noteImageDao.upsert(initialImage1) // Pre-populate one image

        val imagesToUpsert = listOf(
            NoteImageEntity(id = imageId1, noteId = testNoteId2), // This will "update" (replace) existing imageId1
            NoteImageEntity(id = imageId2, noteId = testNoteId1)  // This is a new image
        )
        val resultIds = noteImageDao.upserts(imagesToUpsert)

        assertEquals(2, resultIds.size)
        assertTrue(resultIds.containsAll(listOf(imageId1, imageId2)))

        val image1Retrieved = noteImageDao.get(imageId1).first()
        assertNotNull(image1Retrieved)
        assertEquals(testNoteId2, image1Retrieved!!.noteId) // Check updated noteId

        val image2Retrieved = noteImageDao.get(imageId2).first()
        assertNotNull(image2Retrieved)
        assertEquals(testNoteId1, image2Retrieved!!.noteId)

        val allImages = noteImageDao.getAll().first()
        assertEquals(2, allImages.size) // Only imageId1 (updated) and imageId2 (new) should exist
    }


    @Test
    @Throws(Exception::class)
    fun foreignKeyConstraintOnDeleteNoteCascade() = runTest {
        val imageForCascadeTest = NoteImageEntity(id = imageId1, noteId = testNoteId1)
        noteImageDao.upsert(imageForCascadeTest)

        // Ensure image exists
        assertNotNull(noteImageDao.get(imageId1).first())

        // Delete the parent note
        noteDao.delete(testNoteId1) // Assuming NoteDao has a delete(id: Long) method

        // The image should now be deleted due to CASCADE rule
        assertNull(noteImageDao.get(imageId1).first())

        // Check that images for other notes are not affected
        val imageForNote2 = NoteImageEntity(id = imageId2, noteId = testNoteId2)
        noteImageDao.upsert(imageForNote2)
        assertNotNull(noteImageDao.get(imageId2).first()) // Should still exist
    }
}
