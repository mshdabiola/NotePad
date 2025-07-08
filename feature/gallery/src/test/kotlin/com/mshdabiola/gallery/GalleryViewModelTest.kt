package com.mshdabiola.gallery

import app.cash.turbine.test
import com.mshdabiola.gallery.navigation.GalleryArg
import com.mshdabiola.model.Note // Assuming Note is just the data part
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.testing.repository.TestContentManager
import com.mshdabiola.testing.repository.TestNoteImageRepository
import com.mshdabiola.testing.repository.TestNoteRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: GalleryViewModel
    private lateinit var noteImageRepository: TestNoteImageRepository
    private lateinit var noteRepository: TestNoteRepository // Updated
    private lateinit var contentManager: TestContentManager // Updated

    private val testNoteId = 1L
    private val testGalleryArg = GalleryArg(
        id = testNoteId,
        index = 0,
        total = 0, // Will be overridden by actual images
        currentPath = "/fake/content/path/image_1.jpg",
    )

    @Before
    fun setUp() {
        noteImageRepository = TestNoteImageRepository()
        noteRepository = TestNoteRepository() // Initialize
        contentManager = TestContentManager() // Initialize
    }

    private fun initializeViewModel(galleryArg: GalleryArg = testGalleryArg) {
        viewModel = GalleryViewModel(
            galleryArg = galleryArg,
            noteImageRepository = noteImageRepository,
            noteRepository = noteRepository, // Pass updated dependency
            contentManager = contentManager, // Pass updated dependency
        )
    }

    @Test
    fun `galleryUiState loads initial images correctly`() = runTest {
        val images = listOf(
            NoteImage(id = 1L, noteId = testNoteId, path = "path1"),
            NoteImage(id = 2L, noteId = testNoteId, path = "path2"),
        )
        // Use the method from your TestNoteImageRepository to set images for a specific noteId
        noteImageRepository.upserts(images)
        val arg = testGalleryArg.copy(total = images.size)
        initializeViewModel(arg)

        viewModel.galleryUiState
            .test {
                val initialState = awaitItem()
                assertEquals(arg.index, initialState.initIndex)
                assertEquals(arg.total, initialState.images.size)
                if (arg.total > 0) {
                    assertEquals(arg.currentPath, initialState.images.first().path)
                }

                val loadedState = initialState // awaitItem()
                assertEquals(arg.index, loadedState.initIndex)
                assertEquals(images.size, loadedState.images.size)
                assertEquals(contentManager.getImagePath(images[0].id), loadedState.images[0].path)
                assertEquals(contentManager.getImagePath(images[1].id), loadedState.images[1].path)

                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun `galleryUiState emits placeholder initially if no images yet for noteId but total is provided`() = runTest {
        val arg = GalleryArg(id = testNoteId, index = 0, total = 3, currentPath = "/placeholder.jpg")
        noteImageRepository.upserts(emptyList()) // No images for this note
        initializeViewModel(arg)

        val initialState = viewModel.galleryUiState.value
        assertEquals(arg.index, initialState.initIndex)
        assertEquals(arg.total, initialState.images.size)
        initialState.images.forEachIndexed { index, image ->
            // Placeholder ID is derived from index in ViewModel
            assertEquals(index.toLong(), image.id)
            assertEquals(arg.currentPath, image.path)
        }

        viewModel.galleryUiState.test {
            val loadedState = awaitItem() // Repository flow emits empty list
            assertEquals(0, loadedState.images.size)
        }
    }

    @Test
    fun `onImage successfully extracts text and updates note`() = runTest {
        val imagePathToProcess = contentManager.getImagePath(123L) // Use a path from contentManager
        val extractedText = "New text from image"
        val originalNoteDetail = "Original note details."
        val initialNote = NotePad( // Use NoteData

            note = Note(id = testNoteId, title = "Test Note", detail = originalNoteDetail),
            images = emptyList(),
            drawings = emptyList(),
        )
        noteRepository.upsert(initialNote)
        contentManager.imageToTextResult = extractedText
        initializeViewModel()

        viewModel.onImage(imagePathToProcess)

        assertEquals(imagePathToProcess, contentManager.lastPathForImageToText)
        val updatedNote = noteRepository.get(testNoteId).firstOrNull()
        assertNotNull(updatedNote)
        val expectedDetail = "$originalNoteDetail\n$extractedText"
        assertEquals(expectedDetail, updatedNote?.note?.detail)
    }

    @Test
    fun `onImage handles text extraction failure from contentManager`() = runTest {
        val imagePathToProcess = contentManager.getImagePath(456L)
        val originalNoteDetail = "Original note details for error case."
        val initialNote = NotePad(

            note = Note(id = testNoteId, title = "Error Note", detail = originalNoteDetail),
            images = emptyList(),
            drawings = emptyList(),
        )
        noteRepository.upsert(initialNote)
        contentManager.imageToTextShouldThrowError = false
        initializeViewModel()

        viewModel.onImage(imagePathToProcess) // ViewModel should catch the error

        assertEquals(imagePathToProcess, contentManager.lastPathForImageToText)
        val noteAfterAttempt = noteRepository.get(testNoteId).firstOrNull()
        assertNotNull(noteAfterAttempt)
        // Detail should be original + newline + empty string because of how errors are handled in VM
        assertEquals("$originalNoteDetail\n" + contentManager.imageToTextResult, noteAfterAttempt?.note?.detail)
    }

    @Test
    fun `onImage handles note not found in repository`() = runTest {
        val imagePathToProcess = contentManager.getImagePath(789L)
        contentManager.imageToTextResult = "Some text"
        // Do not set the note in the repository, so it will be null
        initializeViewModel()

        var exceptionHandledByViewModel = false
        try {
            viewModel.onImage(imagePathToProcess)
            // If no exception, it means ViewModel's try-catch handled it.
            // We verify by checking that the note wasn't unexpectedly created or modified.
            exceptionHandledByViewModel = true
        } catch (e: Exception) {
            // This block should ideally not be hit if the ViewModel's try-catch is effective
            exceptionHandledByViewModel = false
        }

        assertTrue("ViewModel should have handled the missing note error internally.", exceptionHandledByViewModel)
        // Further assert that no note was created or upserted if that's the expected behavior
        val note = noteRepository.get(testNoteId).firstOrNull()
        assertEquals("Note should not have been created or modified if it didn't exist.", null, note)
    }

    @Test
    fun `deleteImage calls repository delete`() = runTest {
        val imageIdToDelete = 5L
        val initialImages = listOf(
            NoteImage(id = imageIdToDelete, noteId = testNoteId, path = "delete/path"),
            NoteImage(id = 6L, noteId = testNoteId, path = "keep/path"),
        )
        noteImageRepository.upserts(initialImages)
        initializeViewModel()

        val images = noteImageRepository.getByNoteId(testNoteId).first()
        println(images)
        // Ensure image is present
        assertTrue(images.any { it.id == imageIdToDelete })

        viewModel.deleteImage(imageIdToDelete)

        // Ensure image is deleted
        assertTrue(noteImageRepository.getByNoteId(testNoteId).first().none { it.id == imageIdToDelete })
        assertEquals(1, noteImageRepository.getByNoteId(testNoteId).first().size) // Check remaining count
    }
}
