package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteDao // You'll need to create/import this
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.model.NoteEntity // You'll need to create/import this
import com.mshdabiola.database.model.NotificationEntity
import com.mshdabiola.model.NoteType
import kotlinx.coroutines.flow.first
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
class NoteNotificationDaoTest {
    private lateinit var db: NoteDatabase
    private lateinit var noteNotificationDao: NoteNotificationDao
    private lateinit var noteDao: NoteDao // For prerequisite notes

    // Sample note IDs
    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L

    @Before
    fun before() = runTest { // Make before suspend or use runTest for async operations
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NoteDatabase::class.java)
            // .allowMainThreadQueries() // Not recommended for real tests if DB ops are complex
            .build()
        noteNotificationDao = db.getNotification() // Ensure this method is in NoteDatabase
        noteDao = db.getNoteDao()                     // Ensure this method is in NoteDatabase

        // Insert dummy notes to satisfy foreign key constraints
        testNoteId1 = noteDao.upsert(NoteEntity(id = null, title = "Note 1 for Notification",
            detail = "Detail for Test Note 1",
            editDate = 65,
            isCheck = false,
            color = 4, // Example color
            background = 3,
            isPin = false,
            noteType = NoteType.NOTE))
        testNoteId2 = noteDao.upsert(NoteEntity(id = null, title = "Note 2 for Notification",
            detail = "Detail for Test Note 1",
            editDate = 65,
            isCheck = false,
            color = 4, // Example color
            background = 3,
            isPin = false,
            noteType = NoteType.NOTE))
    }

    @After
    @Throws(IOException::class)
    fun after() {
        db.close()
    }

    private fun createSampleNotification(noteId: Long, customPlaceName: String? = "Work"): NotificationEntity {
        return NotificationEntity(
            noteId = noteId,
            reminderDateTimeStamp = System.currentTimeMillis(),
            placeType = 1, // Work
            customPlaceName = if (customPlaceName == "Work") null else customPlaceName,
            typeIndex = 0, // DoNotRepeat
            intervalEndTypeIndex = 0 // Forever
        )
    }

    @Test
    fun upsertTest() = runTest {
        val notification = createSampleNotification(testNoteId1)
        val insertedId = noteNotificationDao.upsert(notification)

        assertTrue("Inserted ID should be greater than 0", insertedId > 0)

        val retrieved = noteNotificationDao.get(insertedId).first()
        assertNotNull("Retrieved notification should not be null", retrieved)
        assertEquals("Note ID should match", testNoteId1, retrieved!!.noteId)
        assertEquals("Reminder timestamp should match", notification.reminderDateTimeStamp, retrieved.reminderDateTimeStamp)
    }

    @Test
    fun upsertsTest() = runTest {
        val notification1 = createSampleNotification(testNoteId1, "Home")
        val notification2 = createSampleNotification(testNoteId1, "School")
        val notifications = listOf(notification1, notification2)

        val insertedIds = noteNotificationDao.upserts(notifications)
        assertEquals("Should insert 2 notifications", 2, insertedIds.size)
        assertTrue("All inserted IDs should be greater than 0", insertedIds.all { it > 0 })

        val retrieved = noteNotificationDao.getByNoteId(testNoteId1).first()
        assertEquals("Should retrieve 2 notifications for the note", 2, retrieved.size)
    }


    @Test
    fun deleteTest() = runTest {
        val notification = createSampleNotification(testNoteId1)
        val insertedId = noteNotificationDao.upsert(notification)
        assertTrue(insertedId > 0)

        noteNotificationDao.delete(insertedId)
        val retrieved = noteNotificationDao.get(insertedId).first()
        assertNull("Notification should be null after deletion", retrieved)
    }

    @Test
    fun deleteByNoteIdTest() = runTest {
        val notification1Note1 = createSampleNotification(testNoteId1, "Gym")
        val notification2Note1 = createSampleNotification(testNoteId1, "Library")
        val notification1Note2 = createSampleNotification(testNoteId2, "Office")

        noteNotificationDao.upsert(notification1Note1)
        noteNotificationDao.upsert(notification2Note1)
        noteNotificationDao.upsert(notification1Note2)

        noteNotificationDao.deleteByNoteId(testNoteId1)

        val note1Notifications = noteNotificationDao.getByNoteId(testNoteId1).first()
        assertTrue("Notifications for noteId1 should be empty after deletion", note1Notifications.isEmpty())

        val note2Notifications = noteNotificationDao.getByNoteId(testNoteId2).first()
        assertEquals("Notifications for noteId2 should still exist", 1, note2Notifications.size)
    }

    @Test
    fun getOneTest() = runTest {
        val notification = createSampleNotification(testNoteId1, "Custom Place")
        val insertedId = noteNotificationDao.upsert(notification)

        val retrieved = noteNotificationDao.get(insertedId).first()
        assertNotNull(retrieved)
        assertEquals(insertedId, retrieved!!.id)
        assertEquals("Custom Place", retrieved.customPlaceName)
    }

    @Test
    fun getNonExistentReturnsNull() = runTest {
        val retrieved = noteNotificationDao.get(999L).first() // Non-existent ID
        assertNull(retrieved)
    }

    @Test
    fun getAllTest() = runTest {
        // Clear previous notifications to ensure clean state for getAll
        noteNotificationDao.deleteByNoteId(testNoteId1)
        noteNotificationDao.deleteByNoteId(testNoteId2)


        var allNotifications = noteNotificationDao.getAll().first()
        assertTrue("Initially, notification list should be empty", allNotifications.isEmpty())

        val notification1 = createSampleNotification(testNoteId1)
        val notification2 = createSampleNotification(testNoteId2)
        noteNotificationDao.upsert(notification1)
        noteNotificationDao.upsert(notification2)

        allNotifications = noteNotificationDao.getAll().first()
        assertEquals("Should retrieve all 2 inserted notifications", 2, allNotifications.size)
    }

    @Test
    fun getByNoteIdTest() = runTest {
        val notification1Note1 = createSampleNotification(testNoteId1, "Cafe")
        val notification2Note1 = createSampleNotification(testNoteId1, "Park")
        val notification1Note2 = createSampleNotification(testNoteId2, "Store")

        noteNotificationDao.upsert(notification1Note1)
        noteNotificationDao.upsert(notification2Note1)
        noteNotificationDao.upsert(notification1Note2)

        val note1Retrieved = noteNotificationDao.getByNoteId(testNoteId1).first()
        assertEquals("Should retrieve 2 notifications for noteId1", 2, note1Retrieved.size)
        assertTrue(note1Retrieved.any { it.customPlaceName == "Cafe" })
        assertTrue(note1Retrieved.any { it.customPlaceName == "Park" })


        val note2Retrieved = noteNotificationDao.getByNoteId(testNoteId2).first()
        assertEquals("Should retrieve 1 notification for noteId2", 1, note2Retrieved.size)
        assertEquals("Store", note2Retrieved.first().customPlaceName)
    }

    @Test
    fun getByNoteIdWhenNoneExistReturnsEmptyList() = runTest {
        val notifications = noteNotificationDao.getByNoteId(testNoteId1).first()
        assertTrue("Should return empty list for a noteId with no notifications", notifications.isEmpty())
    }

    @Test
    fun upsertUpdatesExistingNotification() = runTest {
        val initialNotification = createSampleNotification(testNoteId1).copy(customPlaceName = "Initial Place")
        val insertedId = noteNotificationDao.upsert(initialNotification)

        val updatedNotification = initialNotification.copy(id = insertedId, customPlaceName = "Updated Place")
        val updatedIdResult = noteNotificationDao.upsert(updatedNotification)
        assertEquals("Upserting with existing ID should return the same ID", insertedId, updatedIdResult)


        val retrieved = noteNotificationDao.get(insertedId).first()
        assertNotNull(retrieved)
        assertEquals("Updated Place", retrieved!!.customPlaceName)
        assertEquals(testNoteId1, retrieved.noteId) // Ensure noteId is not changed
    }

    @Test
    fun foreignKeyCascadeDeleteTest() = runTest {
        val notification = createSampleNotification(testNoteId1)
        val insertedId = noteNotificationDao.upsert(notification)
        assertNotNull(noteNotificationDao.get(insertedId).first())

        // Delete the parent note
        noteDao.delete(testNoteId1) // Assuming NoteDao has delete(id: Long)

        // The notification should now be deleted due to CASCADE rule
        val retrievedAfterCascade = noteNotificationDao.get(insertedId).first()
        assertNull("Notification should be null after parent note deletion due to cascade", retrievedAfterCascade)

        // Ensure notifications for other notes are not affected
        val notificationForNote2 = createSampleNotification(testNoteId2)
        val idNote2 = noteNotificationDao.upsert(notificationForNote2)
        assertNotNull("Notification for other note should still exist", noteNotificationDao.get(idNote2).first())
    }
}
