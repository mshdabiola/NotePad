package com.mshdabiola.selectlabel

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.filter
import app.cash.turbine.test
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteLabel
import com.mshdabiola.selectlabel.navigation.SelectLabelsArgs
import com.mshdabiola.testing.repository.TestLabelRepository
import com.mshdabiola.testing.repository.TestNoteLabelRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class SelectLabelViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var labelRepository: TestLabelRepository
    private lateinit var noteLabelRepository: TestNoteLabelRepository
    private lateinit var viewModel: SelectLabelViewModel

    private val noteId1 = 1L
    private val noteId2 = 2L
    private val sampleArgs = SelectLabelsArgs(ids = "$noteId1,$noteId2")

    private val label1 = Label(id = 101L, name = "Work")
    private val label2 = Label(id = 102L, name = "Personal")
    private val label3 = Label(id = 103L, name = "Urgent")

    @Before
    fun setUp() {
        labelRepository = TestLabelRepository()
        noteLabelRepository = TestNoteLabelRepository()

        // Pre-populate repositories with some data
        runTest {
            labelRepository.upsert(label1)
            labelRepository.upsert(label2)
            labelRepository.upsert(label3)
        }

        viewModel = SelectLabelViewModel(
            selectLabelsArgs = sampleArgs,
            labelRepository = labelRepository,
            noteLabelRepository = noteLabelRepository,
        )
    }

    @Test
    fun `initial selectLabelUiState is correct with no existing note-label associations`() =
        runTest {
            viewModel.selectLabelUiState.test {
                skipItems(1)
                val initialState = awaitItem()
                assertEquals(3, initialState.labels.size)
                assertTrue(initialState.labels.all { it.toggleableState == ToggleableState.Off })
                assertEquals("", initialState.labelQuery.text.toString())
                assertFalse(initialState.showAddLabel)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `selectLabelUiState reflects existing note-label associations`() = runTest {
        // Associate noteId1 with label1 and label2
        // Associate noteId2 with label1
        noteLabelRepository.upsert(NoteLabel(noteId = noteId1, labelId = label1.id))
        noteLabelRepository.upsert(NoteLabel(noteId = noteId1, labelId = label2.id))
        noteLabelRepository.upsert(NoteLabel(noteId = noteId2, labelId = label1.id))

        // Recreate ViewModel to pick up initial repository state if flows are cold or collected once
        // For StateFlows combined from other flows, this might not be strictly necessary
        // if the underlying flows are hot or emit upon subscription.
        // However, to ensure a clean state for this specific test based on pre-populated data:
        viewModel = SelectLabelViewModel(
            selectLabelsArgs = sampleArgs,
            labelRepository = labelRepository,
            noteLabelRepository = noteLabelRepository,
        )

        viewModel.selectLabelUiState.test {
            skipItems(1) // Skip initial state
            val state = awaitItem()
            val label1State = state.labels.find { it.id == label1.id }
            val label2State = state.labels.find { it.id == label2.id }
            val label3State = state.labels.find { it.id == label3.id }

            assertNotNull(label1State)
            assertNotNull(label2State)
            assertNotNull(label3State)

            assertEquals(ToggleableState.On, label1State.toggleableState) // Both notes have label1
            assertEquals(
                ToggleableState.Indeterminate,
                label2State.toggleableState,
            ) // Only noteId1 has label2
            assertEquals(ToggleableState.Off, label3State.toggleableState) // No notes have label3

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `labelQuery filters labels and sets showAddLabel correctly`() = runTest {
        viewModel.selectLabelUiState.test {
            skipItems(1) // Skip initial state
        }
        viewModel.selectLabelUiState.value.labelQuery.setTextAndPlaceCursorAtEnd("Work")
        advanceUntilIdle() // For debounce
        viewModel.selectLabelUiState.test {
            skipItems(1) // Skip initial state
            var state = awaitItem()
            assertEquals(1, state.labels.size)
            assertEquals("Work", state.labels.first().label)
            assertTrue(state.showAddLabel) // "Work" exists, but query is "Work" - should it be true if exact match?
            // The logic is `labels.any { it.name != query }`
            // If "Work" is the query, and "Personal", "Urgent" exist, then `any` is true.}
        }
        viewModel.selectLabelUiState.value.labelQuery.setTextAndPlaceCursorAtEnd("New Label")
        advanceUntilIdle() // For debounce
        viewModel.selectLabelUiState.test {
            skipItems(1)
            val state = awaitItem()
            assertTrue(state.labels.isEmpty()) // No existing label named "New Label"
            assertTrue(state.showAddLabel)
        }

        viewModel.selectLabelUiState.value.labelQuery.setTextAndPlaceCursorAtEnd("")
        advanceUntilIdle() // For debounce

        viewModel.selectLabelUiState.test {
            skipItems(1)
            val state = awaitItem()
            assertEquals(3, state.labels.size) // All labels
            assertFalse(state.showAddLabel)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCheckClick with ToggleableState Off or Indeterminate associates label with all notes`() =
        runTest {
            var personalLabelIndex = -1
            viewModel.selectLabelUiState.test {
                skipItems(1) // Skip initial state
                val initialState = awaitItem()
                personalLabelIndex = initialState.labels.indexOfFirst { it.id == label2.id }
                assertTrue(personalLabelIndex != -1)
                assertEquals(
                    ToggleableState.Off,
                    initialState.labels[personalLabelIndex].toggleableState,
                )
            }
            viewModel.onCheckClick(personalLabelIndex)
            advanceUntilIdle()

            viewModel.selectLabelUiState.test {
                skipItems(1)
                val noteLabelsForLabel2 = noteLabelRepository.getAll().first()
                    .filter { it.labelId == label2.id }
                assertEquals(
                    2,
                    noteLabelsForLabel2.size,
                ) // Both noteId1 and noteId2 should now have label2
                assertTrue(noteLabelsForLabel2.any { it.noteId == noteId1 })
                assertTrue(noteLabelsForLabel2.any { it.noteId == noteId2 })

                val updatedState = awaitItem()
                assertEquals(
                    ToggleableState.On,
                    updatedState.labels[personalLabelIndex].toggleableState,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onCheckClick with ToggleableState On disassociates label from all notes`() = runTest {
        // First, associate label1 with both notes so its state is On
        noteLabelRepository.upsert(NoteLabel(noteId = noteId1, labelId = label1.id))
        noteLabelRepository.upsert(NoteLabel(noteId = noteId2, labelId = label1.id))

        // Re-initialize ViewModel or ensure flows update
        viewModel = SelectLabelViewModel(sampleArgs, labelRepository, noteLabelRepository)
        advanceUntilIdle()

        viewModel.selectLabelUiState.test {
            skipItems(1) // Skip initial state
            val stateWithLabelOn = awaitItem()
            val workLabelIndex = stateWithLabelOn.labels.indexOfFirst { it.id == label1.id }
            assertTrue(workLabelIndex != -1)
            assertEquals(
                ToggleableState.On,
                stateWithLabelOn.labels[workLabelIndex].toggleableState,
            )

            viewModel.onCheckClick(workLabelIndex)
            advanceUntilIdle()

            val noteLabelsForLabel1 = noteLabelRepository.getAll().first()
                .filter { it.labelId == label1.id }
            assertTrue(noteLabelsForLabel1.isEmpty()) // Label1 should be removed from all notes

//            skipItems(1)
            val updatedState = awaitItem()
            assertEquals(
                ToggleableState.Off,
                updatedState.labels[workLabelIndex].toggleableState,
            )

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCreateLabel creates new label, associates with notes, and clears query`() = runTest {
        val newLabelName = "Meetings"
        viewModel.selectLabelUiState.value.labelQuery.setTextAndPlaceCursorAtEnd(newLabelName)
        advanceUntilIdle() // For debounce, though onCreateLabel doesn't directly depend on debounced query for its action

        viewModel.onCreateLabel()
        advanceUntilIdle() // Let repository operations complete

        // Verify label repository
        val allLabels = labelRepository.getAll().first()
        val createdLabel = allLabels.find { it.name == newLabelName }
        assertNotNull(createdLabel, "New label should be created in labelRepository")
        assertNotEquals(-1L, createdLabel.id) // ID should be assigned

        // Verify note-label repository
        val noteLabelsForNewLabel = noteLabelRepository.getAll().first()
            .filter { it.labelId == createdLabel.id }
        assertEquals(2, noteLabelsForNewLabel.size) // Associated with both noteId1 and noteId2
        assertTrue(noteLabelsForNewLabel.any { it.noteId == noteId1 })
        assertTrue(noteLabelsForNewLabel.any { it.noteId == noteId2 })

        // Verify UI State (query cleared, new label might appear in list depending on timing and flow emissions)
        viewModel.selectLabelUiState.test {
            skipItems(1)
            val latestState = awaitItem() // or skip to the relevant emission
            assertEquals(
                "Label query should be cleared",
                "",
                latestState.labelQuery.text.toString(),

            )

            // The new label should eventually appear in the list with ToggleableState.On
            val uiNewLabelState = latestState.labels.find { it.id == createdLabel.id }
            assertNotNull(uiNewLabelState)
            assertEquals(ToggleableState.On, uiNewLabelState!!.toggleableState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // Helper to set text in TextFieldState for tests
    private fun TextFieldState.setTextAndPlaceCursorAtEnd(text: String) {
        this.edit {
            replace(0, length, text)
            // No standard way to place cursor at end in TextFieldState's EditBuffer directly,
            // but for testing the text content, this is usually sufficient.
            // For UI tests, performTextInput handles cursor.
        }
    }
}
