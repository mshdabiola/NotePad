package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteVoiceEntity
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
class NoteVoiceDaoTest {
    private lateinit var db: NoteDatabase
    private lateinit var noteVoiceDao: NoteVoiceDao
    private lateinit var noteDao: NoteDao // For prerequisite notes

    // val noteEntity = Note() // This is from com.mshdabiola.model.Note, not needed for DAO test.
    // We'll use NoteEntity for database interactions.

    // Sample note IDs
    private var testNoteId1: Long = -1L
    private var testNoteId2: Long = -1L

    // Sample voice IDs (since NoteVoiceEntity.id is not auto-generated)
    private val voiceId1: Long = 100L
    private val voiceId2: Long = 101L
    private val voiceId3: Long = 102L

    @Before
    fun before() = runTest { // Use runTest for suspending operations in @Before
        val content = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(content, NoteDatabase::class.java)
            // .allowMainThreadQueries() // Avoid if possible, even in tests
            .build()
        noteVoiceDao = db.getNoteVoiceDao() // Ensure this method is in NoteDatabase
        noteDao = db.getNoteDao() // Ensure this method is in NoteDatabase

        // Insert dummy notes to satisfy foreign key constraints
        testNoteId1 = noteDao.upsert(
            NoteEntity(
                id = null, title = "Note 1 for Voice",
                detail = "Detail for Test Note 1",
                editDate = 65,
                isCheck = false,
                color = 4, // Example color
                background = 3,
                isPin = false,
                noteType = NoteType.NOTE,
            ),
        )
        testNoteId2 = noteDao.upsert(
            NoteEntity(
                id = null, title = "Note 2 for Voice", detail = "Detail for Test Note 1",
                editDate = 65,
                isCheck = false,
                color = 4, // Example color
                background = 3,
                isPin = false,
                noteType = NoteType.NOTE,
            ),
        )
    }

    @After
    @Throws(IOException::class)
    fun after() {
        db.close()
    }

    private fun createSampleVoice(id: Long, noteId: Long, voiceNameSuffix: String): NoteVoiceEntity {
        return NoteVoiceEntity(
            id = id,
            noteId = noteId,
            voiceName = "voice_recording_$voiceNameSuffix.mp3",
        )
    }

    @Test
    fun upsertTest() = runTest {
        val voice = createSampleVoice(voiceId1, testNoteId1, "alpha")
        val insertedId = noteVoiceDao.upsert(voice)

        assertEquals("Inserted ID should match the provided ID", voiceId1, insertedId)

        val retrieved = noteVoiceDao.get(voiceId1).first()
        assertNotNull("Retrieved voice should not be null", retrieved)
        assertEquals("Note ID should match", testNoteId1, retrieved!!.noteId)
        assertEquals("Voice name should match", voice.voiceName, retrieved.voiceName)
    }

    @Test
    fun upsertsTest() = runTest {
        val voice1 = createSampleVoice(voiceId1, testNoteId1, "bravo")
        val voice2 = createSampleVoice(voiceId2, testNoteId1, "charlie")
        val voices = listOf(voice1, voice2)

        val insertedIds = noteVoiceDao.upserts(voices)
        assertEquals("Should insert 2 voice entries", 2, insertedIds.size)
        assertTrue("Inserted IDs should contain the provided IDs", insertedIds.containsAll(listOf(voiceId1, voiceId2)))

        val retrieved = noteVoiceDao.getByNoteId(testNoteId1).first()
        assertEquals("Should retrieve 2 voice entries for the note", 2, retrieved.size)
    }

    @Test
    fun deleteTest() = runTest { // This tests delete by NoteVoiceEntity's ID
        val voice = createSampleVoice(voiceId1, testNoteId1, "delta")
        noteVoiceDao.upsert(voice)

        noteVoiceDao.delete(voiceId1) // delete by ID
        val retrieved = noteVoiceDao.get(voiceId1).first()
        assertNull("Voice should be null after deletion", retrieved)
    }

    @Test
    fun deleteByNoteIdTest() = runTest { // Changed from deleteByIdTest to reflect DAO method
        val voice1Note1 = createSampleVoice(voiceId1, testNoteId1, "echo")
        val voice2Note1 = createSampleVoice(voiceId2, testNoteId1, "foxtrot")
        val voice1Note2 = createSampleVoice(voiceId3, testNoteId2, "golf")

        noteVoiceDao.upsert(voice1Note1)
        noteVoiceDao.upsert(voice2Note1)
        noteVoiceDao.upsert(voice1Note2)

        noteVoiceDao.deleteByNoteId(testNoteId1)

        val note1Voices = noteVoiceDao.getByNoteId(testNoteId1).first()
        assertTrue("Voices for noteId1 should be empty after deletion", note1Voices.isEmpty())

        val note2Voices = noteVoiceDao.getByNoteId(testNoteId2).first()
        assertEquals("Voices for noteId2 should still exist", 1, note2Voices.size)
        assertEquals(voiceId3, note2Voices.first().id)
    }

    @Test
    fun getOneTest() = runTest {
        val voice = createSampleVoice(voiceId1, testNoteId1, "hotel")
        noteVoiceDao.upsert(voice)

        val retrieved = noteVoiceDao.get(voiceId1).first()
        assertNotNull(retrieved)
        assertEquals(voiceId1, retrieved!!.id)
        assertEquals("voice_recording_hotel.mp3", retrieved.voiceName)
    }

    @Test
    fun getNonExistentReturnsNull() = runTest {
        val retrieved = noteVoiceDao.get(9999L).first() // Non-existent ID
        assertNull(retrieved)
    }

    @Test
    fun getAllTest() = runTest {
        // Clear previous entries
        noteVoiceDao.deleteByNoteId(testNoteId1)
        noteVoiceDao.deleteByNoteId(testNoteId2)

        var allVoices = noteVoiceDao.getAll().first()
        assertTrue("Initially, voice list should be empty", allVoices.isEmpty())

        val voice1 = createSampleVoice(voiceId1, testNoteId1, "india")
        val voice2 = createSampleVoice(voiceId2, testNoteId2, "juliett")
        noteVoiceDao.upsert(voice1)
        noteVoiceDao.upsert(voice2)

        allVoices = noteVoiceDao.getAll().first()
        assertEquals("Should retrieve all 2 inserted voice entries", 2, allVoices.size)
    }

    @Test
    fun getByNoteIdTest() = runTest {
        val voice1Note1 = createSampleVoice(voiceId1, testNoteId1, "kilo")
        val voice2Note1 = createSampleVoice(voiceId2, testNoteId1, "lima")
        val voice1Note2 = createSampleVoice(voiceId3, testNoteId2, "mike")

        noteVoiceDao.upsert(voice1Note1)
        noteVoiceDao.upsert(voice2Note1)
        noteVoiceDao.upsert(voice1Note2)

        val note1Retrieved = noteVoiceDao.getByNoteId(testNoteId1).first()
        assertEquals("Should retrieve 2 voices for noteId1", 2, note1Retrieved.size)
        assertTrue(note1Retrieved.any { it.id == voiceId1 && it.voiceName.contains("kilo") })
        assertTrue(note1Retrieved.any { it.id == voiceId2 && it.voiceName.contains("lima") })

        val note2Retrieved = noteVoiceDao.getByNoteId(testNoteId2).first()
        assertEquals("Should retrieve 1 voice for noteId2", 1, note2Retrieved.size)
        assertEquals(voiceId3, note2Retrieved.first().id)
        assertTrue(note2Retrieved.first().voiceName.contains("mike"))
    }

    @Test
    fun getByNoteIdWhenNoneExistReturnsEmptyList() = runTest {
        val voices = noteVoiceDao.getByNoteId(testNoteId1).first() // testNoteId1 has no voices yet in this test
        assertTrue("Should return empty list for a noteId with no voices", voices.isEmpty())
    }

    @Test
    fun upsertUpdatesExistingVoice() = runTest {
        // Since ID is not auto-generated, an upsert with the same ID will replace the existing entry.
        val initialVoice = createSampleVoice(voiceId1, testNoteId1, "november_old")
        noteVoiceDao.upsert(initialVoice)

        val updatedVoice = createSampleVoice(voiceId1, testNoteId1, "november_new") // Same ID, different name
        val updatedIdResult = noteVoiceDao.upsert(updatedVoice)
        assertEquals("Upserting with existing ID should return the same ID", -1, updatedIdResult)

        val retrieved = noteVoiceDao.get(voiceId1).first()
        assertNotNull(retrieved)
        assertEquals("voice_recording_november_new.mp3", retrieved!!.voiceName)
        assertEquals(testNoteId1, retrieved.noteId) // Ensure noteId is not changed if not intended
    }

    @Test
    fun foreignKeyCascadeDeleteTest() = runTest {
        val voice = createSampleVoice(voiceId1, testNoteId1, "oscar")
        noteVoiceDao.upsert(voice)
        assertNotNull(noteVoiceDao.get(voiceId1).first())

        // Delete the parent note
        noteDao.delete(testNoteId1) // Assuming NoteDao has delete(id: Long)

        // The voice entry should now be deleted due to CASCADE rule
        val retrievedAfterCascade = noteVoiceDao.get(voiceId1).first()
        assertNull("Voice entry should be null after parent note deletion due to cascade", retrievedAfterCascade)

        // Ensure voice entries for other notes are not affected
        val voiceForNote2 = createSampleVoice(voiceId2, testNoteId2, "papa")
        noteVoiceDao.upsert(voiceForNote2)
        assertNotNull("Voice entry for other note should still exist", noteVoiceDao.get(voiceId2).first())
    }
}
