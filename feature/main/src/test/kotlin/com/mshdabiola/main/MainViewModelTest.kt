/*
 *abiola 2022
 */

package com.mshdabiola.main

import app.cash.turbine.test
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import com.mshdabiola.testing.repository.TestContentManager
import com.mshdabiola.testing.repository.TestLabelRepository
import com.mshdabiola.testing.repository.TestNoteCheckRepository
import com.mshdabiola.testing.repository.TestNoteDrawingRepository
import com.mshdabiola.testing.repository.TestNoteImageRepository
import com.mshdabiola.testing.repository.TestNoteLabelRepository
import com.mshdabiola.testing.repository.TestNoteRepository
import com.mshdabiola.testing.repository.TestNoteVoiceRepository
import com.mshdabiola.testing.repository.TestNotificationRepository
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Fakes from your :testing module
    private lateinit var noteRepository: TestNoteRepository
    private lateinit var userDataRepository: TestUserDataRepository
    private lateinit var labelRepository: TestLabelRepository
    private lateinit var noteCheckRepository: TestNoteCheckRepository
    private lateinit var noteDrawingRepository: TestNoteDrawingRepository
    private lateinit var noteImageRepository: TestNoteImageRepository
    private lateinit var noteLabelRepository: TestNoteLabelRepository
    private lateinit var noteNotificationRepository: TestNotificationRepository
    private lateinit var noteVoiceRepository: TestNoteVoiceRepository
    private lateinit var contentManager: TestContentManager

    // Real UseCases using the fake repositories
    private lateinit var getAllNoteUseCase: GetAllNoteUseCase
    private lateinit var addAllNoteUseCase: AddAllNoteUseCase

    private lateinit var viewModel: MainViewModel

    private val testLabel1 = Label(id = 100L, name = "Work")
    private val testLabel2 = Label(id = 101L, name = "Personal")

    // Test Data
    private val testNote1 = Note(
        id = 1L,
        title = "Note 1",
        detail = "Content 1",
        isPin = false,
        color = 0,
        noteType = NoteType.NOTE,
    )
    private val testNotePad1 = NotePad(note = testNote1, labels = listOf(testLabel1), notification = null)

    private val testPinnedNote2 = Note(
        id = 2L,
        title = "Pinned Note 2",
        detail = "Content 2",
        isPin = true,
        color = 1,
        noteType = NoteType.NOTE,
    )
    private val testPinnedNotePad2 = NotePad(note = testPinnedNote2, labels = emptyList(), notification = null)

    private val testArchivedNote3 = Note(id = 3L, title = "Archived Note 3", detail = "Content 3", isPin = false, color = 2, noteType = NoteType.ARCHIVE)
    private val testArchivedNotePad3 = NotePad(note = testArchivedNote3, labels = emptyList(), notification = null)

    private val testTrashedNote4 = Note(id = 4L, title = "Trashed Note 4", detail = "Content 4", isPin = false, color = 3, noteType = NoteType.TRASH)
    private val testTrashedNotePad4 = NotePad(note = testTrashedNote4, labels = emptyList(), notification = null)

    private val defaultDisplayCategory = NoteDisplayCategory(0, NoteType.NOTE) // All notes
    private val label1DisplayCategory = NoteDisplayCategory(testLabel1.id, NoteType.LABEL)
    private val archiveDisplayCategory = NoteDisplayCategory(0, NoteType.ARCHIVE)
    private val trashDisplayCategory = NoteDisplayCategory(0, NoteType.TRASH)
    val defaultUserData = UserData(
        isGrid = false,
        noteDisplayCategory = defaultDisplayCategory,
        darkThemeConfig = DarkThemeConfig.DARK,
        themeBrand = ThemeBrand.DEFAULT,
        useDynamicColor = false,
        shouldHideOnboarding = false,
        contrast = Contrast.Medium,

    )

    @Before
    fun setup() {
        noteRepository = TestNoteRepository()
        userDataRepository = TestUserDataRepository()
        labelRepository = TestLabelRepository()
        noteCheckRepository = TestNoteCheckRepository()
        noteDrawingRepository = TestNoteDrawingRepository()
        noteImageRepository = TestNoteImageRepository()
        noteLabelRepository = TestNoteLabelRepository()
        noteNotificationRepository = TestNotificationRepository()
        noteVoiceRepository = TestNoteVoiceRepository()
        contentManager = TestContentManager()

        // Initialize UseCases with the fake repositories
        getAllNoteUseCase = GetAllNoteUseCase(
            noteRepository = noteRepository,
            linkUriUseCase = LinkUriUseCase(), // Assuming LinkUriUseCase doesn't need fakes or is simple
            contentManager = contentManager,
        )
        addAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = noteRepository,
            noteCheckRepository = noteCheckRepository,
            noteDrawingRepository = noteDrawingRepository,
            noteImageRepository = noteImageRepository,
            noteLabelRepository = noteLabelRepository,
            noteNotificationRepository = noteNotificationRepository,
            noteVoiceRepository = noteVoiceRepository,
        )

        viewModel = MainViewModel(
            noteRepository = noteRepository,
            userDataRepository = userDataRepository,
            labelRepository = labelRepository,
            getAllNoteUseCase = getAllNoteUseCase,
            addAllNoteUseCase = addAllNoteUseCase,
        )
    }

    @Test
    fun `initial state is Loading then Success with default data`() = runTest {
        viewModel.mainState.test {
            assertEquals(MainState.Loading, awaitItem())

            // Set initial user data for default behavior
            userDataRepository.setUserData(
                UserData(
                    isGrid = false,
                    noteDisplayCategory = defaultDisplayCategory,
                    darkThemeConfig = DarkThemeConfig.DARK,
                    themeBrand = ThemeBrand.DEFAULT,
                    useDynamicColor = false,
                    shouldHideOnboarding = false,
                    contrast = Contrast.Medium,
                ),
            )

            val successState = awaitItem()
            assertTrue(successState is MainState.Success)
            val successData = successState as MainState.Success
            assertTrue(successData.pinNotePads.isEmpty())
            assertTrue(successData.unPinNotePads.isEmpty())
            assertNull(successData.labelName)
            assertEquals(defaultDisplayCategory, successData.noteDisplayCategory)
            assertNull(successData.selectState)
            assertFalse(successData.isGrid)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mainState reflects notes based on display category`() = runTest {
        // Add notes
        noteRepository.upserts(listOf(testNotePad1, testPinnedNotePad2, testArchivedNotePad3))
        val ids = labelRepository.upserts(listOf(testLabel1)) // For label display test
        viewModel.mainState.test {
            assertEquals(MainState.Loading, awaitItem()) // Initial Loading

            // 1. Default: All (unarchived, untrashed) notes
            userDataRepository.setUserData(
                UserData(
                    isGrid = false,
                    noteDisplayCategory = defaultDisplayCategory,
                    darkThemeConfig = DarkThemeConfig.DARK,
                    themeBrand = ThemeBrand.DEFAULT,
                    useDynamicColor = false,
                    shouldHideOnboarding = false,
                    contrast = Contrast.Medium,

                ),
            )
            val defaultState = awaitItem() as MainState.Success
            assertEquals(1, defaultState.pinNotePads.size)
            assertTrue(defaultState.pinNotePads.any { it.note.id == testPinnedNote2.id })
            assertEquals(1, defaultState.unPinNotePads.size)
            assertTrue(defaultState.unPinNotePads.any { it.note.id == testNote1.id })
            assertNull(defaultState.labelName)
        }

        // 2. Archived notes
        userDataRepository.setNoteDisplayCategory(archiveDisplayCategory)
        viewModel.mainState.test {
            val archiveState = awaitItem() as MainState.Success // Skip intermediate states if any
            assertTrue(archiveState.pinNotePads.isEmpty()) // Archived notes are not pinned in archive view typically
            assertEquals(1, archiveState.unPinNotePads.size)
            assertTrue(archiveState.unPinNotePads.any { it.note.id == testArchivedNote3.id })
        }

        // 3. Notes with specific label (assuming testNotePad1 gets testLabel1)
        // First, associate testNotePad1 with testLabel1
        noteLabelRepository.upserts(listOf(NoteLabel(testNote1.id, testLabel1.id)))

        userDataRepository.setNoteDisplayCategory(label1DisplayCategory) // Switch to label view

        viewModel.mainState.test {
            val labelState = awaitItem() as MainState.Success
            assertEquals(testLabel1.name, labelState.labelName)
            // Assuming pinned notes are still shown if they have the label
            // and the getAllNoteUseCase filters correctly by label.
            // Adjust if your logic differs.
            if (testPinnedNotePad2.labels.any { it.id == testLabel1.id }) { // If pinned note also has label1
                assertTrue(labelState.pinNotePads.any { it.note.id == testPinnedNote2.id })
            } else {
                assertTrue(labelState.pinNotePads.isEmpty())
            }
            assertEquals(1, labelState.unPinNotePads.size)
            assertTrue(labelState.unPinNotePads.any { it.note.id == testNote1.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleCardSelection toggles selection and updates selectState`() = runTest {
        noteRepository.upserts(listOf(testNotePad1, testPinnedNotePad2))
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
            //   awaitItem() // Loading
            val initialState = awaitItem() as MainState.Success
            assertNull(initialState.selectState)

            // Select first note
            viewModel.handleCardSelection(testNote1.id)

            val firstSelectionState = awaitItem() as MainState.Success
            assertNotNull(firstSelectionState.selectState)
            assertEquals(1, firstSelectionState.selectState?.setOfSelected?.size)
            assertTrue(firstSelectionState.selectState?.setOfSelected?.contains(testNote1.id) == true)
            assertFalse(firstSelectionState.selectState!!.isAllPin)
            assertEquals(testNote1.color, firstSelectionState.selectState!!.colorIndex)
            assertEquals(testNotePad1.notification, firstSelectionState.selectState!!.notificationUiState)

            // Select second note (pinned)
            viewModel.handleCardSelection(testPinnedNote2.id)
            val secondSelectionState = awaitItem() as MainState.Success
            assertEquals(2, secondSelectionState.selectState?.setOfSelected?.size)
            assertTrue(secondSelectionState.selectState?.setOfSelected?.contains(testNote1.id) == true)
            assertTrue(secondSelectionState.selectState?.setOfSelected?.contains(testPinnedNote2.id) == true)
            // isAllPin should be false because one is not pinned, colorIndex and notification for multiple selection is -1/null
            assertFalse(secondSelectionState.selectState!!.isAllPin)
            assertEquals(-1, secondSelectionState.selectState!!.colorIndex)
            assertNull(secondSelectionState.selectState!!.notificationUiState)

            // Deselect first note
            viewModel.handleCardSelection(testNote1.id)
            val thirdSelectionState = awaitItem() as MainState.Success
            assertEquals(1, thirdSelectionState.selectState?.setOfSelected?.size)
            assertTrue(thirdSelectionState.selectState?.setOfSelected?.contains(testPinnedNote2.id) == true)
            assertTrue(thirdSelectionState.selectState!!.isAllPin) // Now only pinned is selected
            assertEquals(testPinnedNote2.color, thirdSelectionState.selectState!!.colorIndex)
            assertEquals(testPinnedNotePad2.notification, thirdSelectionState.selectState!!.notificationUiState)

            // Deselect second note (last one) - should clear selection
            viewModel.handleCardSelection(testPinnedNote2.id)
            val finalState = awaitItem() as MainState.Success
            assertNull(finalState.selectState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deselectNotes clears selection`() = runTest {
        noteRepository.upserts(listOf(testNotePad1))
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
//            awaitItem() // Loading
            awaitItem() // Initial Success

            viewModel.handleCardSelection(testNote1.id)
            val selectedState = awaitItem() as MainState.Success
            assertNotNull(selectedState.selectState)

            viewModel.deselectNotes()
            val deselectedState = awaitItem() as MainState.Success
            assertNull(deselectedState.selectState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pinOrUnpinNotes toggles pin status of selected notes`() = runTest {
        noteRepository.upserts(listOf(testNotePad1, testPinnedNotePad2)) // Note1 (unpinned), PinnedNote2 (pinned)
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
//            awaitItem() // Loading
            awaitItem() // Initial Success

            // Select unpinned note
            viewModel.handleCardSelection(testNote1.id)
            awaitItem() // Selection state update

            viewModel.pinOrUnpinNotes() // Should pin it
            skipItems(1) // deselect
            val pinnedState = awaitItem() as MainState.Success // State after pinning and deselection
            val updatedNote1 = noteRepository.get(testNote1.id)!!.first()!!.note
            assertTrue(updatedNote1.isPin)
            assertNull(pinnedState.selectState) // Selection should be cleared

            // Select already pinned note
            viewModel.handleCardSelection(testPinnedNote2.id)
            awaitItem() // Selection state update

            viewModel.pinOrUnpinNotes() // Should unpin it
            skipItems(1) // deselect
            val unpinnedState = awaitItem() as MainState.Success
            val updatedNote2 = noteRepository.get(testPinnedNote2.id)!!.first()!!.note
            assertFalse(updatedNote2.isPin)
            assertNull(unpinnedState.selectState)

            // Select both, one pinned one unpinned. Should pin the unpinned one.
            noteRepository.upserts(listOf(testNotePad1.copy(note = testNotePad1.note.copy(isPin = false)))) // reset note1 to unpinned
            noteRepository.upserts(listOf(testPinnedNotePad2.copy(note = testPinnedNotePad2.note.copy(isPin = true)))) // reset note2 to pinned

            viewModel.handleCardSelection(testNote1.id)
            awaitItem()
            viewModel.handleCardSelection(testPinnedNote2.id)
            awaitItem() // Both selected

            viewModel.pinOrUnpinNotes() // Should pin testNote1
            skipItems(2) // deselect

            val mixedPinState = awaitItem() as MainState.Success
            val finalNote1 = noteRepository.get(testNote1.id)!!.first()!!.note
            val finalNote2 = noteRepository.get(testPinnedNote2.id)!!.first()!!.note
            assertFalse(finalNote1.isPin)
            assertTrue(finalNote2.isPin) // Note2 remains pinned
            assertNull(mixedPinState.selectState)

            cancelAndIgnoreRemainingEvents()
        }
        // Verify addAllNoteUseCase was called
    }

    @Test
    fun `setAllColor updates color of selected notes`() = runTest {
        val newColor = 5
        noteRepository.upserts(listOf(testNotePad1))
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
            viewModel.handleCardSelection(testNote1.id)
            awaitItem() // Selection state

            viewModel.setAllColor(newColor)
            skipItems(2)
            val updatedState = awaitItem() as MainState.Success
            val updatedNote = noteRepository.get(testNote1.id)!!.first()!!.note
            assertEquals(newColor, updatedNote.color)
            assertNull(updatedState.selectState) // Selection cleared

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onArchiveNote moves selected notes to archive and back`() = runTest {
        noteRepository.upserts(listOf(testNotePad1, testArchivedNotePad3)) // Regular and one already archived
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
            // 1. Archive testNote1
            viewModel.handleCardSelection(testNote1.id)
            awaitItem()
            viewModel.onArchiveNote()
            skipItems(2)
            val archivedState = awaitItem() as MainState.Success
            val updatedNote1 = noteRepository.get(testNote1.id)!!.first()!!.note
            assertEquals(NoteType.ARCHIVE, updatedNote1.noteType)
            assertNull(archivedState.selectState)
            // Verify it's removed from the default view
            assertFalse(archivedState.unPinNotePads.any { it.note.id == testNote1.id })

            // 2. Unarchive testNote1 (which is now testArchivedNotePad3 effectively)
            // Switch view to Archive to select it
            userDataRepository.setNoteDisplayCategory(archiveDisplayCategory)
            skipItems(1)
            val archiveView = awaitItem() as MainState.Success // State reflects archive view
            assertTrue(archiveView.unPinNotePads.any { it.note.id == testNote1.id }) // Should now be in archive list

            viewModel.handleCardSelection(testNote1.id) // Select the (now archived) note1
            awaitItem()

            viewModel.onArchiveNote() // This should unarchive it
            skipItems(1)
            val unarchivedState = awaitItem() as MainState.Success // State reflects archive view but note is gone
            val unarchivedNote1 = noteRepository.get(testNote1.id)!!.first()!!.note
            assertEquals(NoteType.NOTE, unarchivedNote1.noteType)
            assertNull(unarchivedState.selectState)
            assertFalse(unarchivedState.unPinNotePads.any { it.note.id == testNote1.id }) // No longer in archive view

            // Switch back to default view to see it
            userDataRepository.setNoteDisplayCategory(defaultDisplayCategory)
            skipItems(1)
            val defaultViewAgain = awaitItem() as MainState.Success
            assertTrue(defaultViewAgain.unPinNotePads.any { it.note.id == testNote1.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteNote moves selected notes to trash`() = runTest {
        noteRepository.upserts(listOf(testNotePad1.copy(note = testNote1.copy(isPin = true)))) // Pinned note
        userDataRepository.setUserData(defaultUserData)
        viewModel.mainState.test {
            viewModel.handleCardSelection(testNote1.id)
            awaitItem()

            viewModel.onDeleteNote()
            skipItems(2)
            val trashedState = awaitItem() as MainState.Success
            val updatedNote = noteRepository.get(testNote1.id)!!.first()!!.note
            assertEquals(NoteType.TRASH, updatedNote.noteType)
            assertFalse(updatedNote.isPin) // Should be unpinned when trashed
            assertNull(trashedState.selectState)
            assertTrue(trashedState.pinNotePads.isEmpty()) // No longer in pinned view
            assertTrue(trashedState.unPinNotePads.isEmpty()) // No longer in unpinned view (for NOTE type)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteForever deletes selected notes from repository`() = runTest {
        noteRepository.upserts(listOf(testTrashedNotePad4)) // Note in trash
        userDataRepository.setUserData(defaultUserData.copy(noteDisplayCategory = trashDisplayCategory)) // View trash

        viewModel.mainState.test {
            val initialTrash = awaitItem() as MainState.Success
            assertTrue(initialTrash.unPinNotePads.any { it.note.id == testTrashedNote4.id })

            viewModel.handleCardSelection(testTrashedNote4.id)
            awaitItem()

            viewModel.onDeleteForever()
            skipItems(1)
            val afterDeleteState = awaitItem() as MainState.Success
            assertNull(noteRepository.get(testTrashedNote4.id)?.first())
            assertNull(afterDeleteState.selectState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRestore moves selected notes from trash to notes`() = runTest {
        noteRepository.upserts(listOf(testTrashedNotePad4))
        userDataRepository.setUserData(defaultUserData.copy(noteDisplayCategory = trashDisplayCategory)) // View trash

        viewModel.mainState.test {
            viewModel.handleCardSelection(testTrashedNote4.id)
            awaitItem()

            viewModel.onRestore()
            skipItems(2)
            val afterRestoreTrashView = awaitItem() as MainState.Success
            val restoredNote = noteRepository.get(testTrashedNote4.id)!!.first()!!.note
            assertEquals(NoteType.NOTE, restoredNote.noteType)
            assertNull(afterRestoreTrashView.selectState)
            assertTrue(afterRestoreTrashView.unPinNotePads.isEmpty()) // Gone from trash view

            // Check in default view
            userDataRepository.setNoteDisplayCategory(defaultDisplayCategory)
            skipItems(1)
            val defaultView = awaitItem() as MainState.Success
            assertTrue(defaultView.unPinNotePads.any { it.note.id == testTrashedNote4.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCopyNote creates a copy of the selected note`() = runTest {
        noteRepository.upserts(listOf(testNotePad1))
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
            viewModel.handleCardSelection(testNote1.id)
            awaitItem()

            val initialNotesCount = getAllNoteUseCase(defaultDisplayCategory).first().size

            viewModel.onCopyNote()
            skipItems(2)
            val afterCopyState = awaitItem() as MainState.Success // State with the copied note

            val notesAfterCopy = getAllNoteUseCase(defaultDisplayCategory).first()
            assertEquals(initialNotesCount + 1, notesAfterCopy.size)
            assertNull(afterCopyState.selectState) // Selection is cleared

            // Verify the copied note (it will have a new ID, check content)
            val copiedNotePad = notesAfterCopy.find { it.note.id != testNote1.id && it.note.title == testNote1.title }
            assertNotNull(copiedNotePad)
            assertEquals(testNote1.title, copiedNotePad!!.note.title)
            assertEquals(testNote1.detail, copiedNotePad.note.detail)
            assertNotEquals(testNote1.id, copiedNotePad.note.id) // Ensure ID is new

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteLabel deletes label and resets display category`() = runTest {
        labelRepository.upserts(listOf(testLabel1))
        noteRepository.upserts(listOf(testNotePad1)) // Add a note to make the state emit
        // Set current display to the label we want to delete
        userDataRepository.setUserData(defaultUserData.copy(noteDisplayCategory = label1DisplayCategory))

        viewModel.mainState.test {
            val initialLabelState = awaitItem() as MainState.Success
            assertEquals(testLabel1.name, initialLabelState.labelName)
            assertEquals(label1DisplayCategory, initialLabelState.noteDisplayCategory)

            viewModel.deleteLabel()

            // Expect two emissions:
            // 1. UserData updated to default display category
            // 2. Label actually deleted, potentially triggering note refresh if that affects filtering
            val afterUserDataUpdate = awaitItem() as MainState.Success
            assertEquals(defaultDisplayCategory, afterUserDataUpdate.noteDisplayCategory) // UserData reset

            // The label might be deleted before or after the userData flows,
            // so we might get another emission if the notes list changes due to the label deletion.
            // Or it might be part of the same emission as above if getAllNoteUseCase reacts quickly.
            // For simplicity, we check the repository directly.
//            val finalState = awaitItem() as MainState.Success // Or skipIfNoFurtherEmissions
            assertNull(labelRepository.get(testLabel1.id)?.first()) // Label is gone
            assertNull(afterUserDataUpdate.labelName) // Label name should be null now

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `renameLabel updates label name`() = runTest {
        val newName = "Updated Work"
        labelRepository.upserts(listOf(testLabel1))
        noteRepository.upserts(listOf(testNotePad1)) // Add a note
        userDataRepository.setUserData(defaultUserData.copy(noteDisplayCategory = label1DisplayCategory))

        viewModel.mainState.test {
            val initialLabelState = awaitItem() as MainState.Success
            assertEquals(testLabel1.name, initialLabelState.labelName)

            viewModel.renameLabel(newName)

//            val renamedState = awaitItem() as MainState.Success // State reflects new name
//            assertEquals(newName, renamedState.labelName)
            val updatedLabel = labelRepository.get(testLabel1.id)?.first()
            assertEquals(newName, updatedLabel?.name)
            assertTrue(labelRepository.getAll().first().any { it.id == testLabel1.id && it.name == newName })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteAllTrash deletes all notes of type TRASH`() = runTest {
        noteRepository.upserts(listOf(testTrashedNotePad4, testNotePad1.copy(note = testNotePad1.note.copy(noteType = NoteType.TRASH, id = 5L))))
        userDataRepository.setUserData(defaultUserData.copy(noteDisplayCategory = trashDisplayCategory))

        viewModel.mainState.test {
            val initialTrashState = awaitItem() as MainState.Success
            assertEquals(2, initialTrashState.unPinNotePads.size) // Both trash notes

            viewModel.onDeleteAllTrash()

            val emptyTrashState = awaitItem() as MainState.Success
            assertTrue(emptyTrashState.unPinNotePads.isEmpty())
            // Verify notes are actually gone from repository
            assertNull(noteRepository.get(testTrashedNotePad4.note.id)?.first())
            assertNull(noteRepository.get(5L)?.first())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDisplayModeChange toggles isGrid in UserData`() = runTest {
        userDataRepository.setUserData(defaultUserData.copy(isGrid = false, noteDisplayCategory = label1DisplayCategory))
        noteRepository.upserts(listOf(testNotePad1)) // Ensure state emits

        viewModel.mainState.test {
            val initialState = awaitItem() as MainState.Success
            assertFalse(initialState.isGrid)

            viewModel.onDisplayModeChange()

            val toggledState = awaitItem() as MainState.Success
            assertTrue(toggledState.isGrid)

            // Toggle back
            viewModel.onDisplayModeChange()
            val toggledBackState = awaitItem() as MainState.Success
            assertFalse(toggledBackState.isGrid)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSendNote returns selected note and deselects`() = runTest {
        noteRepository.upserts(listOf(testNotePad1, testPinnedNotePad2))
        userDataRepository.setUserData(defaultUserData)

        viewModel.mainState.test {
            awaitItem() // Initial Success

            // Select a note
            viewModel.handleCardSelection(testNotePad1.note.id)
            val selectedState = awaitItem() as MainState.Success
            assertNotNull(selectedState.selectState)

            // Call onSendNote
            val sentNotepad = viewModel.onSendNote()

            // Verify returned note
            assertEquals(testNotePad1.note.id, sentNotepad.note.id)
            assertEquals(testNotePad1.note.title, sentNotepad.note.title)

            // Verify selection is cleared
            val deselectedState = awaitItem() as MainState.Success // State emits after deselectNotes
            assertNull(deselectedState.selectState)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
