package com.mshdabiola.label

import app.cash.turbine.test
import com.mshdabiola.labelscreen.LabelArg
import com.mshdabiola.labelscreen.LabelState
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteType
import com.mshdabiola.testing.repository.TestLabelRepository
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LabelViewModelTest {

    @get:Rule(1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var labelRepository: TestLabelRepository
    private lateinit var userDataRepository: TestUserDataRepository // Use FakeUserDataRepository
    private lateinit var viewModel: LabelViewModel

    private val testLabelArg = LabelArg(isEditMode = false)
    private val testLabelArgEditMode = LabelArg(isEditMode = true)

    @Before
    fun setUp() {
        labelRepository = TestLabelRepository()
        userDataRepository = TestUserDataRepository() // Initialize the fake

        // No default coEvery needed as the FakeUserDataRepository has its own default state
    }

    private fun initViewModel(labelArg: LabelArg = testLabelArg) {
        viewModel = LabelViewModel(labelArg, labelRepository, userDataRepository)
    }

    @Test
    fun `initial state reflects labelArg and empty labels`() = runTest {
        initViewModel()
        viewModel.labelUiState.test {
            val initialState = awaitItem()
            assertEquals(persistentListOf<LabelState>(), initialState.labels)
            assertEquals("", initialState.newLabel.label.text)
            assertEquals(false, initialState.isEditMode)
        }
    }

    @Test
    fun `initial state reflects labelArg with isEditMode true`() = runTest {
        initViewModel(testLabelArgEditMode)
        viewModel.labelUiState.test {
            val initialState = awaitItem()
            assertEquals(true, initialState.isEditMode)
        }
    }

    @Test
    fun `labels from TestLabelRepository updates uiState`() = runTest {
        val mockLabelsFromDb = listOf(
            Label(id = 1L, name = "Label One"),
            Label(id = 2L, name = "Label Two"),
        )
        labelRepository.upserts(mockLabelsFromDb)

        initViewModel()

        viewModel.labelUiState.test {
            val updatedState = awaitItem()

            assertEquals(2, updatedState.labels.size)
            assertEquals("Label One", updatedState.labels[0].label.text.toString())
            assertEquals("Label Two", updatedState.labels[1].label.text.toString())
        }
    }

    // IMPORTANT: This test assumes the ViewModel's onAddNew(-1) is fixed
    // to save the current text before resetting.
    @Test
    fun `onAddNew with index -1 (new label) calls repository upsert and resets newLabel`() = runTest {
        initViewModel()
        val newLabelText = "My New Label"

        val currentNewLabelTextFieldState = viewModel.labelUiState.value.newLabel.label
        currentNewLabelTextFieldState.edit { append(newLabelText) }

        viewModel.onAddNew(-1)

        val labelsInRepo = labelRepository.getAll().first()
        val addedLabel = labelsInRepo.find { it.name == newLabelText }
        assertNotNull("Label should have been added to the repository", addedLabel)
        assertEquals(newLabelText, addedLabel?.name)

        viewModel.labelUiState.test {
            // The state will emit multiple times:
            // 1. Initial state.
            // 2. State after text edit (if combine triggers, depends on TextFieldState identity).
            // 3. State after newLabel.value = LabelState() in onAddNew.
            // We are interested in the state *after* the reset.
            // Awaiting specific conditions or using `expectMostRecentItem()` after actions can be more robust.
            val finalState = expectMostRecentItem() // Get the latest state after all operations
            assertEquals("New label text field should be reset", "", finalState.newLabel.label.text.toString())
        }
    }

    @Test
    fun `onAddNew with valid index calls repository upsert with existing label value`() = runTest {
        val existingLabelModel = Label(id = 1L, name = "Existing 1")
        labelRepository.upserts(listOf(existingLabelModel))
        initViewModel()

        viewModel.labelUiState.test {
            val currentState = awaitItem()
            val labelToUpdateIndex = 0
            val updatedLabelStateFromUi = currentState.labels[labelToUpdateIndex]
            val updatedText = "Updated Name"

            updatedLabelStateFromUi.label.edit { replace(0, updatedLabelStateFromUi.label.text.length, updatedText) }

            viewModel.onAddNew(labelToUpdateIndex)

            val labelsInRepo = labelRepository.getAll().first()
            val updatedLabelInRepo = labelsInRepo.find { it.id == existingLabelModel.id }
            assertEquals(updatedText, updatedLabelInRepo?.name)
            this.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDelete calls repository delete`() = runTest {
        initViewModel()
        val labelToDelete = Label(id = 123L, name = "To Delete")
        labelRepository.upserts(listOf(labelToDelete))

        println("all ${labelRepository.getAll().first()}")
        viewModel.onDelete(labelToDelete.id)

        delay(1000)
        val labelsAfterDelete = labelRepository.getAll().first()
        assertFalse("Label should have been deleted", labelsAfterDelete.any { it.id == labelToDelete.id })
    }

    @Test
    fun `onDelete also updates UserData if deleted label was the active display category`() = runTest {
        val labelIdToDelete = 456L
        val labelToDelete = Label(id = labelIdToDelete, name = "Active Label")
        labelRepository.upserts(listOf(labelToDelete))

        userDataRepository.setNoteDisplayCategory(NoteDisplayCategory(noteType = NoteType.LABEL, labelId = labelIdToDelete)) // Set initial state in the fake
        initViewModel()

        viewModel.onDelete(labelIdToDelete)

        val labelsAfterDelete = labelRepository.getAll().first()
        assertFalse(labelsAfterDelete.any { it.id == labelIdToDelete })

        assertEquals(NoteDisplayCategory(), userDataRepository.userData.first().noteDisplayCategory)
    }

    @Test
    fun `onDelete does NOT update UserData if deleted label was NOT active display category`() = runTest {
        val labelIdToDelete = 789L
        val otherActiveLabelId = 111L
        labelRepository.upserts(listOf(Label(id = labelIdToDelete, name = "Some Label")))

        userDataRepository.setNoteDisplayCategory(NoteDisplayCategory(noteType = NoteType.LABEL, labelId = otherActiveLabelId))
        initViewModel()

        viewModel.onDelete(labelIdToDelete)
        assertEquals(NoteType.LABEL, userDataRepository.userData.first().noteDisplayCategory.noteType)
    }

    @Test
    fun `onDelete does NOT update UserData if active display category is not LABEL type`() = runTest {
        val labelIdToDelete = 789L
        labelRepository.upserts(listOf(Label(id = labelIdToDelete, name = "Another Label")))

        userDataRepository.setNoteDisplayCategory(NoteDisplayCategory(noteType = NoteType.NOTE, labelId = 3))
        initViewModel()

        viewModel.onDelete(labelIdToDelete)

        assertEquals(NoteType.NOTE, userDataRepository.userData.first().noteDisplayCategory.noteType)
    }
}
