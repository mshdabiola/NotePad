package com.mshdabiola.drawing

import app.cash.turbine.test
import com.mshdabiola.drawing.navigation.DrawingArgs
import com.mshdabiola.model.DrawingPath
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.testing.repository.TestNoteDrawingRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DrawingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var drawingRepository: TestNoteDrawingRepository
    private lateinit var viewModel: DrawingViewModel

    private val testDrawingArgs = DrawingArgs(noteId = 1L, id = null)
    private val testExistingDrawingArgs = DrawingArgs(noteId = 2L, id = 100L)

    @Before
    fun setUp() {
        drawingRepository = TestNoteDrawingRepository()
    }

    private fun initializeViewModel(args: DrawingArgs = testDrawingArgs) {
        viewModel = DrawingViewModel(
            drawing = args,
            drawingRepository = drawingRepository,
        )
    }

    @Test
    fun `when new drawing is created, it's upserted with a new id`() = runTest {
        initializeViewModel()

        val testPath = DrawingPath()
        viewModel.controller.drawingPaths.add(testPath)

        viewModel.drawingState.test {
            // Initial state
            assertEquals(DrawingUiState(), awaitItem())

            skipItems(1)
            val firstEmission = awaitItem()
            assertNotNull("Drawing ID should be assigned", firstEmission.drawingId)
            assertEquals(1, firstEmission.drawings.size)
            assertEquals(testPath, firstEmission.drawings.first())

            // Verify it was saved in the repository
            val savedDrawing = drawingRepository.get(firstEmission.drawingId!!).first()
            assertNotNull(savedDrawing)
            assertEquals(1, savedDrawing?.drawingPaths?.size)
            assertEquals(testPath, savedDrawing?.drawingPaths?.first())
            assertEquals(testDrawingArgs.noteId, savedDrawing?.noteId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when existing drawing is loaded, its paths are displayed`() = runTest {
        // Pre-populate repository with an existing drawing
        val existingPath = DrawingPath()
        val existingDrawing = NoteDrawing(
            id = testExistingDrawingArgs.id!!,
            noteId = testExistingDrawingArgs.noteId,
            drawingPaths = listOf(existingPath),
        )
        drawingRepository.upsert(existingDrawing)

        initializeViewModel(testExistingDrawingArgs)

        viewModel.drawingState.test {
            // Initial state
            assertEquals(DrawingUiState(), awaitItem())
            advanceTimeBy(600) // Advance past debounce for initial load if any drawing paths were added immediately

            val loadedState = awaitItem()
            assertEquals(testExistingDrawingArgs.id, loadedState.drawingId)
            assertEquals(1, loadedState.drawings.size)
            assertEquals(existingPath, loadedState.drawings.first())

            // Also check controller's paths
            assertTrue(viewModel.controller.drawingPaths.contains(existingPath))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when drawing path is added, state updates and drawing is upserted`() = runTest {
        initializeViewModel()

        val path1 = DrawingPath()
        viewModel.controller.drawingPaths.add(path1)
        advanceTimeBy(600)

        viewModel.drawingState.test {
            assertEquals(DrawingUiState(), awaitItem()) // Initial
            skipItems(1)
            val state1 = awaitItem()
            assertNotNull(state1.drawingId)
            assertEquals(1, state1.drawings.size)
            assertEquals(path1, state1.drawings.first())
            val firstDrawingId = state1.drawingId

            // Verify save
            val savedDrawing = drawingRepository.get(firstDrawingId!!).first()
            assertNotNull(savedDrawing)
            assertEquals(1, savedDrawing?.drawingPaths?.size)
        }

        val path2 = DrawingPath()
        viewModel.controller.drawingPaths.add(path2)
        advanceTimeBy(600)

        viewModel.drawingState.test {
            val state1 = awaitItem()
            val firstDrawingId = state1.drawingId!!

            val state2 = awaitItem()
            assertEquals(firstDrawingId, state2.drawingId) // ID should remain the same
            assertEquals(2, state2.drawings.size)
            assertTrue(state2.drawings.contains(path1))
            assertTrue(state2.drawings.contains(path2))

            // Verify save
            val savedDrawing = drawingRepository.get(firstDrawingId).first()
            assertNotNull(savedDrawing)
            assertEquals(2, savedDrawing?.drawingPaths?.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteImage removes drawing from repository`() = runTest {
        // Create an initial drawing
        initializeViewModel()
        val testPath = DrawingPath()
        viewModel.controller.drawingPaths.add(testPath)
        advanceTimeBy(600) // Ensure it's saved

        viewModel.drawingState.test {
            skipItems(1)
            val state = awaitItem()
            val drawingId = state.drawingId
            assertNotNull(drawingId)
            assertNotNull(drawingRepository.get(drawingId!!).first())

            viewModel.deleteDrawing()

            assertNull(drawingRepository.get(drawingId).first())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `controller drawingPaths are initialized from existing drawing`() = runTest {
        val existingPath1 = DrawingPath()
        val existingPath2 = DrawingPath()
        val existingDrawing = NoteDrawing(
            id = testExistingDrawingArgs.id!!,
            noteId = testExistingDrawingArgs.noteId,
            drawingPaths = listOf(existingPath1, existingPath2),
        )
        drawingRepository.upsert(existingDrawing)

        initializeViewModel(testExistingDrawingArgs)

        // Wait for the state to settle and paths to be loaded
        viewModel.drawingState.test {
            awaitItem() // initial
            val loadedState = awaitItem() // after load

            assertEquals(2, viewModel.controller.drawingPaths.size)
            assertTrue(viewModel.controller.drawingPaths.contains(existingPath1))
            assertTrue(viewModel.controller.drawingPaths.contains(existingPath2))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
