import app.cash.turbine.test
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.Label
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import com.mshdabiola.playnotepad.MainActivityUiState
import com.mshdabiola.playnotepad.MainActivityViewModel
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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDataRepository: TestUserDataRepository
    private lateinit var noteRepository: TestNoteRepository
    private lateinit var labelRepository: TestLabelRepository
    private lateinit var addAllNoteUseCase: AddAllNoteUseCase
    private lateinit var contentManager: TestContentManager // Use the TestContentManager
    private lateinit var viewModel: MainActivityViewModel

    val defaultUserData = UserData(
        isGrid = true,
        noteDisplayCategory = NoteDisplayCategory(),
        darkThemeConfig = DarkThemeConfig.DARK,
        themeBrand = ThemeBrand.DEFAULT,
        useDynamicColor = false,
        shouldHideOnboarding = false,
        contrast = Contrast.Medium,

    )
    private val sampleLabels = listOf(
        Label(id = 1, name = "Label1"),
        Label(id = 2, name = "Label2"),
    )

    @Before
    fun setUp() {
        userDataRepository = TestUserDataRepository()
        labelRepository = TestLabelRepository()
        noteRepository = TestNoteRepository()
        addAllNoteUseCase = AddAllNoteUseCase(
            noteRepository = noteRepository,
            noteCheckRepository = TestNoteCheckRepository(),
            noteDrawingRepository = TestNoteDrawingRepository(),
            noteImageRepository = TestNoteImageRepository(),
            noteLabelRepository = TestNoteLabelRepository(),
            noteNotificationRepository = TestNotificationRepository(),
            noteVoiceRepository = TestNoteVoiceRepository(),
        )
        contentManager = TestContentManager() // Instantiate the fake

        userDataRepository.setUserData(defaultUserData)
        labelRepository.setData(sampleLabels)

        viewModel = MainActivityViewModel(
            userDataRepository = userDataRepository,
            labelRepository = labelRepository,
            addNoteUseCase = addAllNoteUseCase,
            contentManager = contentManager, // Pass the fake
        )
    }

    @Test
    fun `uiState emits Loading then Success with initial UserData`() = runTest {
        assertEquals(MainActivityUiState.Loading, viewModel.uiState.value)

        viewModel.uiState.test {
            val successState = awaitItem() as MainActivityUiState.Success
            assertEquals(defaultUserData, successState.userData)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `labels emits initial labels from repository`() = runTest {
        viewModel.labels.test {
            assertEquals(sampleLabels, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertNewNote calls addAllNoteUseCase and returns its result`() = runTest {
        val expectedNoteId = 1L
//        addAllNoteUseCase.setNextExpectedId(expectedNoteId)

        val actualNoteId = viewModel.insertNewNote()

        assertEquals(expectedNoteId, actualNoteId)
        val capturedNotePad = noteRepository.get(expectedNoteId).first()!!
        assertNotNull(capturedNotePad)
        assertTrue(capturedNotePad.note.detail.isEmpty())
        assertTrue(capturedNotePad.voices.isEmpty())
        assertTrue(capturedNotePad.images.isEmpty())
        assertTrue(capturedNotePad.checks.isEmpty())
    }

    @Test
    fun `insertNewAudioNote calls contentManager and addAllNoteUseCase`() = runTest {
        val testUri = "content://audio/1"
        val testText = "Audio note text"
        val savedVoiceId = "voice_abc"
        val expectedNoteId = 1L

        contentManager.saveVoice(savedVoiceId) // Configure fake

        val actualNoteId = viewModel.insertNewAudioNote(testUri, testText)

        assertEquals(expectedNoteId, actualNoteId)

        val capturedNotePad = noteRepository.get(expectedNoteId).first()!!
        assertNotNull(capturedNotePad)
        assertEquals(testText, capturedNotePad.note.detail)
        assertEquals(1, capturedNotePad.voices.size)
    }

    @Test
    fun `insertNewImageNote calls contentManager and addAllNoteUseCase`() = runTest {
        val testUri = "content://image/1"
        val expectedNoteId = 1L

        contentManager.saveImage(testUri) // Configure fake

        val actualNoteId = viewModel.insertNewImageNote(testUri)

        assertEquals(expectedNoteId, actualNoteId)

        val capturedNotePad = noteRepository.get(expectedNoteId).first()!!
        assertNotNull(capturedNotePad)
        assertEquals(1, capturedNotePad.images.size)
    }

    @Test
    fun `insertNewDrawing calls addAllNoteUseCase`() = runTest {
        val expectedNoteId = 1L

        val actualNoteId = viewModel.insertNewDrawing()

        assertEquals(expectedNoteId, actualNoteId)
        val capturedNotePad = noteRepository.get(expectedNoteId).first()!!
        assertNotNull(capturedNotePad)
    }

    @Test
    fun `insertNewCheckNote calls addAllNoteUseCase with check properties`() = runTest {
        val expectedNoteId = 1L

        val actualNoteId = viewModel.insertNewCheckNote()

        assertEquals(expectedNoteId, actualNoteId)
        val capturedNotePad = noteRepository.get(expectedNoteId).first()!!
        assertNotNull(capturedNotePad)
        assertTrue(capturedNotePad.note?.isCheck == true)
        assertEquals(1, capturedNotePad.checks?.size)
    }

    @Test
    fun `pictureUri calls contentManager pictureUri`() {
        val expectedUri = ""
        contentManager.saveImage(expectedUri) // Configure fake

        val actualUri = viewModel.pictureUri()

        assertEquals(expectedUri, actualUri)
    }

    @Test
    fun `setMainData calls userDataRepository setNoteDisplayCategory`() = runTest {
        val newDisplayCategory = NoteDisplayCategory()

        viewModel.setMainData(newDisplayCategory)
        advanceUntilIdle()

        val updatedUserData = userDataRepository.userData.first()
        assertEquals(newDisplayCategory, updatedUserData.noteDisplayCategory)
    }
}
