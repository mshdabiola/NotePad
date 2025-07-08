/*
 *abiola 2022
 */

package com.mshdabiola.detail

import app.cash.turbine.test
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.DateUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteCheck
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.testing.repository.TestContentManager
import com.mshdabiola.testing.repository.TestNoteCheckRepository
import com.mshdabiola.testing.repository.TestNoteDrawingRepository
import com.mshdabiola.testing.repository.TestNoteImageRepository
import com.mshdabiola.testing.repository.TestNoteLabelRepository
import com.mshdabiola.testing.repository.TestNoteRepository
import com.mshdabiola.testing.repository.TestNoteVoiceRepository
import com.mshdabiola.testing.repository.TestNotificationRepository
import com.mshdabiola.testing.repository.TestVoicePlayer
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var noteRepository: TestNoteRepository
    private lateinit var noteCheckRepository: TestNoteCheckRepository
    private lateinit var noteVoiceRepository: TestNoteVoiceRepository
    private lateinit var addAllNoteUseCase: AddAllNoteUseCase
    private lateinit var getNoteUseCase: GetNoteUseCase
    private lateinit var viewModel: DetailViewModel

    private val testDetailArg = DetailArg(id = 1L, colorIndex = 0, background = 0)
    private val initialNote = Note(id = testDetailArg.id, title = "Initial Title", detail = "Initial Detail")
    private val initialNotePad = NotePad(note = initialNote)

    @Before
    fun setup() {
        noteRepository = TestNoteRepository()
        noteCheckRepository = TestNoteCheckRepository()
        noteVoiceRepository = TestNoteVoiceRepository()

        val linkUriUseCase = LinkUriUseCase()
        val contentManager = TestContentManager()

        getNoteUseCase = GetNoteUseCase(
            noteRepository = noteRepository,
            linkUriUseCase = linkUriUseCase,
            contentManager = contentManager,
        )

        addAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = noteRepository,
            noteCheckRepository = noteCheckRepository,
            noteDrawingRepository = TestNoteDrawingRepository(),
            noteImageRepository = TestNoteImageRepository(),
            noteLabelRepository = TestNoteLabelRepository(),
            noteNotificationRepository = TestNotificationRepository(),
            noteVoiceRepository = noteVoiceRepository,
        )

        // Pre-populate repository for tests that load existing notes
        runTest {
            noteRepository.upsert(initialNotePad)
        }
    }

    private fun initializeViewModel(arg: DetailArg = testDetailArg) {
        viewModel = DetailViewModel(
            detailArg = arg,
            voicePlayer = TestVoicePlayer(),
            getNoteUseCase = getNoteUseCase,
            contentManager = TestContentManager(),
            addAllNoteUseCase = addAllNoteUseCase,
            dateUseCase = DateUseCase(),
            noteCheckRepository = noteCheckRepository,
            noteVoiceRepository = noteVoiceRepository,
        )
    }

    @Test
    fun `init loads existing note and updates state`() = runTest {
        initializeViewModel()
        viewModel.detailState.test {
            var state = awaitItem() // Initial state from constructor

            assertEquals(testDetailArg.id, state.notePad.note.id)
            // Skip initial default state, wait for note to load
            val loadedState = awaitItem()
            assertEquals(initialNote.title, loadedState.notePad.note.title)
            assertEquals(initialNote.detail, loadedState.notePad.note.detail)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init with new note id creates and saves a new note`() = runTest {
        val newNoteArg = DetailArg(id = 0L, colorIndex = -1, background = -1) // 0L or -1L typically indicates new
        initializeViewModel(newNoteArg)

        viewModel.detailState.test {
            skipItems(1) // Skip initial default state from constructor
            val state = awaitItem()
            assertTrue(state.notePad.note.id > 0) // Assert new ID is generated
            assertEquals(newNoteArg.colorIndex, state.notePad.note.color)
            assertEquals(newNoteArg.background, state.notePad.note.background)

            val savedNote = noteRepository.get(state.notePad.note.id).first()
            assertNotNull(savedNote)
            assertEquals(state.notePad.note.id, savedNote.note.id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addCheck adds a new check item and updates repository`() = runTest {
        initializeViewModel()
        viewModel.detailState.first() // Ensure ViewModel is initialized and note loaded

        viewModel.addCheck()
        advanceUntilIdle() // Allow coroutines to complete

        val checks = noteCheckRepository.getByNoteId(testDetailArg.id).first()
        assertEquals(1, checks.size)
        assertFalse(checks[0].isCheck)

        viewModel.detailState.test {
            // The state might take a moment to reflect the new check due to how checks are added to initState
            // This part of the test might need adjustment based on how DetailState updates its checks list
            skipItems(1) // Current state
            val updatedState = awaitItem()
            assertTrue(updatedState.unChecks.any { !it.isCheck && it.noteId == testDetailArg.id })
            // For now, we verified repository state, which is more reliable here.
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCheckDelete removes a check item`() = runTest {
        initializeViewModel()
        val checkId = noteCheckRepository.upsert(NoteCheck(noteId = testDetailArg.id, content = "test check"))
        advanceUntilIdle()

        viewModel.onCheckDelete(checkId)
        advanceUntilIdle()
//
        val checks = noteCheckRepository.getByNoteId(testDetailArg.id).first()
        assertTrue(checks.none { it.id == checkId })
    }

    @Test
    fun `changeToCheckBoxes converts detail text to checks`() = runTest {
        val noteWithDetail = initialNote.copy(detail = "Item 1\nItem 2", isCheck = false)
        addAllNoteUseCase(initialNotePad.copy(note = noteWithDetail))
        initializeViewModel()
        viewModel.detailState.test { // Wait for initial load
            awaitItem()
            val state = awaitItem()
            assertEquals("Item 1\nItem 2", state.notePad.note.detail)
        }

        viewModel.changeToCheckBoxes()
        advanceUntilIdle()

        viewModel.detailState.test {
            awaitItem()
            val state = awaitItem()
            assertEquals("", state.notePad.note.detail)
            assertTrue(state.notePad.note.isCheck)
            val checks = noteCheckRepository.getByNoteId(testDetailArg.id).first()
            assertEquals(2, checks.size)
            assertTrue(checks.any { it.content == "Item 1" })
            assertTrue(checks.any { it.content == "Item 2" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteCheckedItems removes checked items`() = runTest {
        noteCheckRepository.upserts(
            listOf(
                NoteCheck(noteId = testDetailArg.id, content = "checked", isCheck = true),
                NoteCheck(noteId = testDetailArg.id, content = "unchecked", isCheck = false),
            ),
        )

        initializeViewModel()

        advanceUntilIdle()
        viewModel.detailState.first() // Ensure state is updated with checks if applicable

        // Manually update initState for the test as ViewModel does this internally
        val currentState = viewModel.detailState.value
        currentState.checks.add(NoteCheckUiState(id = 1L, noteId = testDetailArg.id, isCheck = true).apply { content.edit { append("checked") } })
        currentState.unChecks.add(NoteCheckUiState(id = 2L, noteId = testDetailArg.id, isCheck = false).apply { content.edit { append("unchecked") } })

        viewModel.deleteCheckedItems()
        advanceUntilIdle()

        val checks = noteCheckRepository.getByNoteId(testDetailArg.id).first()
        assertEquals(1, checks.size)
        assertEquals("unchecked", checks[0].content)
        assertTrue(viewModel.detailState.value.checks.isEmpty())
    }

    @Test
    fun `hideCheckBoxes converts checks back to detail text`() = runTest {
        val noteWithChecks = initialNote.copy(isCheck = true, detail = "")
        addAllNoteUseCase(initialNotePad.copy(note = noteWithChecks))

        noteCheckRepository.upserts(
            listOf(
                NoteCheck(noteId = testDetailArg.id, content = "Item A", isCheck = false),
                NoteCheck(noteId = testDetailArg.id, content = "Item B", isCheck = true),
            ),
        )
        initializeViewModel()
        viewModel.detailState.test { // Wait for initial load with checks
            awaitItem()
            val state = awaitItem()

            assertTrue(state.notePad.note.isCheck)
        }
        // Manually update initState for the test
        val currentState = viewModel.detailState.value
        currentState.unChecks.add(NoteCheckUiState(id = 1L, noteId = testDetailArg.id, isCheck = false).apply { content.edit { append("Item A") } })
        currentState.checks.add(NoteCheckUiState(id = 2L, noteId = testDetailArg.id, isCheck = true).apply { content.edit { append("Item B") } })

        viewModel.hideCheckBoxes()
        advanceUntilIdle()

        viewModel.detailState.test {
            skipItems(2)
            val state = awaitItem()
            assertFalse(state.notePad.note.isCheck)
            assertTrue(state.notePad.note.detail.contains("Item A"))
            assertTrue(state.notePad.note.detail.contains("Item B"))
            val checksInRepo = noteCheckRepository.getByNoteId(testDetailArg.id).first()
            assertTrue(checksInRepo.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pinNote toggles isPin state`() = runTest {
        initializeViewModel()
        val initialPinState = viewModel.detailState.first().notePad.note.isPin

        viewModel.pinNote()
        advanceUntilIdle()

        viewModel.detailState.test {
            skipItems(1) // Current state after init
            assertEquals(!initialPinState, awaitItem().notePad.note.isPin)
            cancelAndIgnoreRemainingEvents()
        }
        val savedNote = noteRepository.get(testDetailArg.id).first()?.note
        assertEquals(!initialPinState, savedNote?.isPin)
    }

    @Test
    fun `onColorChange updates note color`() = runTest {
        initializeViewModel()
        val newColorIndex = 5

        viewModel.onColorChange(newColorIndex)
        advanceUntilIdle()

        viewModel.detailState.test {
            skipItems(1)
            assertEquals(newColorIndex, awaitItem().notePad.note.color)
            cancelAndIgnoreRemainingEvents()
        }
        val savedNote = noteRepository.get(testDetailArg.id).first()?.note
        assertEquals(newColorIndex, savedNote?.color)
    }

    @Test
    fun `onImageChange updates note background image`() = runTest {
        initializeViewModel()
        val newImageIndex = 3

        viewModel.onImageChange(newImageIndex)
        advanceUntilIdle()

        viewModel.detailState.test {
            skipItems(1)
            assertEquals(newImageIndex, awaitItem().notePad.note.background)
            cancelAndIgnoreRemainingEvents()
        }
        val savedNote = noteRepository.get(testDetailArg.id).first()?.note
        assertEquals(newImageIndex, savedNote?.background)
    }

    @Test
    fun `onArchive moves note to archive and back`() = runTest {
        initializeViewModel()
        // To Archive
        viewModel.onArchive()
        advanceUntilIdle()
        viewModel.detailState.test {
            skipItems(1)
            assertEquals(NoteType.ARCHIVE, awaitItem().notePad.note.noteType)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(
            NoteType.ARCHIVE,
            noteRepository.get(testDetailArg.id).first()?.note?.noteType,
        )

        // From Archive back to Note
        viewModel.onArchive()
        advanceUntilIdle()
        viewModel.detailState.test {
            skipItems(1)
            assertEquals(NoteType.NOTE, awaitItem().notePad.note.noteType)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(NoteType.NOTE, noteRepository.get(testDetailArg.id).first()?.note?.noteType)
    }

    @Test
    fun `onTrash moves note to trash`() = runTest {
        initializeViewModel()
        viewModel.onTrash()
        advanceUntilIdle()

        viewModel.detailState.test {
            skipItems(1)
            assertEquals(NoteType.TRASH, awaitItem().notePad.note.noteType)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(NoteType.TRASH, noteRepository.get(testDetailArg.id).first()?.note?.noteType)
    }

    @Test
    fun `copyNote creates a new note with same content but new id`() = runTest {
        initializeViewModel()
        val originalNote = viewModel.detailState.first { it.notePad.note.id == testDetailArg.id }.notePad

        viewModel.copyNote()
        advanceUntilIdle()

        // Verify a new note was added to the repository
        val allNotes = noteRepository.getAll().first()
        assertEquals(2, allNotes.size) // Initial note + copied note
        val copiedNotePad = allNotes.find { it.note.id != originalNote.note.id }
        assertNotNull(copiedNotePad)
        assertEquals(originalNote.note.title, copiedNotePad.note.title)
        assertEquals(originalNote.note.detail, copiedNotePad.note.detail)
        assertNotEquals(originalNote.note.id, copiedNotePad.note.id)
        assertTrue(copiedNotePad.note.id > 0) // Should have a new valid ID
    }

    @Test
    fun `deleteVoiceNote removes a voice note`() = runTest {
        val voiceNote = NoteVoice(id = 10L, noteId = testDetailArg.id, path = "path/to/voice.mp3", length = 1000L)
        val noteWithVoice = initialNote.copy()
        val notePadWithVoice = initialNotePad.copy(note = noteWithVoice, voices = listOf(voiceNote))
        addAllNoteUseCase(notePadWithVoice)

        initializeViewModel()
        viewModel.detailState.test { // Wait for load
            awaitItem()
            val state = awaitItem()
            println(state)
            assertEquals(1, state.notePad.voices.size)
        }

        viewModel.deleteVoiceNote(0) // Delete the first (and only) voice note
        advanceUntilIdle()

        viewModel.detailState.test {
            skipItems(1)
            val state = awaitItem()
            assertTrue(state.notePad.voices.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
        val voiceNotesInRepo = noteVoiceRepository.getByNoteId(testDetailArg.id).first()
        println(voiceNotesInRepo)
        assertTrue(voiceNotesInRepo.isEmpty())
    }
}
