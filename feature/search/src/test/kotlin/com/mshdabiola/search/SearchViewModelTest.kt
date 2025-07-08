package com.mshdabiola.search

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import app.cash.turbine.test
import com.mshdabiola.domain.GetAllNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.IntervalEnd
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NotificationInterval
import com.mshdabiola.model.NotificationUiState
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import com.mshdabiola.testing.repository.TestContentManager
import com.mshdabiola.testing.repository.TestNoteRepository
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SearchViewModelTest {

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: SearchViewModel
    private lateinit var userDataRepository: TestUserDataRepository
    private lateinit var noteRepository: TestNoteRepository
    private lateinit var getAllNoteUseCase: GetAllNoteUseCase // Actual UseCase

    private val labelWork = Label(101, "Work")
    private val labelPersonal = Label(102, "Personal")
    private val labelUrgent = Label(103, "Urgent")

    // Sample notes to be inserted into TestNoteRepository
    private val note1 = Note(1, "Title 1", "Content Apple", color = 0, isCheck = true)
    private val note2 = Note(2, "Title 2", "Content Banana", color = 1)
    private val note3 = Note(3, "Title 3", "Content Cherry", color = 0, isCheck = false)
    private val note4Reminder = Note(4, "Title Reminder", "Content Date")
    val defaultUserData = UserData(
        isGrid = true,
        noteDisplayCategory = NoteDisplayCategory(),
        darkThemeConfig = DarkThemeConfig.DARK,
        themeBrand = ThemeBrand.DEFAULT,
        useDynamicColor = false,
        shouldHideOnboarding = false,
        contrast = Contrast.Medium,

    )

    @OptIn(ExperimentalTime::class)
    private val sampleNoteMinsFromRepo = listOf(
        NotePad(note1, labels = listOf(labelWork)),
        NotePad(note2, labels = listOf(labelPersonal)),
        NotePad(note3, labels = listOf(labelWork, labelUrgent)),
        NotePad(
            note4Reminder,
            labels = emptyList(),
            notification = NotificationUiState(
                noteId = 5,
                currentDateTime = Instant.fromEpochMilliseconds(
                    455788,
                ).toLocalDateTime(
                    TimeZone.currentSystemDefault(),
                ),
                currentPlace = null,
                currentInterval = NotificationInterval.Monthly(
                    sameDay = true,
                    interval = "1",
                    intervalEnd = IntervalEnd.Forever,
                ),
            ),
        ),
    )

    @Before
    fun setUp() { // setUp can be a suspend function with runTest
        userDataRepository = TestUserDataRepository()
        noteRepository = TestNoteRepository()
        val linkUriUseCase = LinkUriUseCase() // Assuming it's simple and stateless
        val contentManager = TestContentManager() // Using test content manager
        getAllNoteUseCase = GetAllNoteUseCase(
            noteRepository = noteRepository,
            linkUriUseCase = linkUriUseCase,
            contentManager = contentManager,
        )

        viewModel = SearchViewModel(userDataRepository, getAllNoteUseCase)
    }

    @Test
    fun `initial searchState is Select with correct categories when query is blank`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)

        viewModel.searchState.test {
            awaitItem()
            val initialState = awaitItem() // Give time for initial notes to load through use case
            assertTrue(initialState is SearchState.Select)
            val selectState = initialState as SearchState.Select

            assertEquals(6, selectState.types.size) // Standard types

            // Check labels (derived from notes in TestNoteRepository)
            val expectedLabels = listOf(labelWork, labelPersonal, labelUrgent)
            assertEquals(expectedLabels.size, selectState.label.size)
            assertTrue(selectState.label.any { it.name == "Work" && it.id == labelWork.id })
            assertTrue(selectState.label.any { it.name == "Personal" && it.id == labelPersonal.id })
            assertTrue(selectState.label.any { it.name == "Urgent" && it.id == labelUrgent.id })

            // Check colors (derived from notes in TestNoteRepository)
            val expectedColors = sampleNoteMinsFromRepo.map { it.note.color }.distinct().sorted()
            assertEquals(expectedColors.size, selectState.color.size)
            assertTrue(selectState.color.any { it.colorIndex == 0 })
            assertTrue(selectState.color.any { it.colorIndex == 1 })
        }
    }

    @Test
    fun `searchState transitions to Success when query is entered`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchQuery.edit {
            append("Apple")
        }
        viewModel.searchState.test {
            awaitItem() // Initial Select state after notes load

//            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300) // Advance past debounce

            val successState = awaitItem()
            assertTrue(successState is SearchState.Success)
            val successResults = (successState as SearchState.Success).searches
            assertEquals(1, successResults.size)
            assertEquals(note1.title, successResults.first().note.title)
            assertTrue(successState.isGrid)
        }
    }

    @Test
    fun `searchState reflects isGrid from UserDataRepository`() = runTest {
//
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchQuery.edit {
            append("Banana")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)

        viewModel.searchState.test {
            awaitItem()
            // The flow might emit initial Select, then Success. We want the latest Success.
            val state = awaitItem()
            assertTrue("Expected Success state, but got $state", state is SearchState.Success)
            val successState = state as SearchState.Success
            assertTrue("isGrid should be true", successState.isGrid)
            assertEquals(1, successState.searches.size) // Note 2 contains "Banana"
            assertEquals(note2.title, successState.searches.first().note.title)
        }
    }

    @Test
    fun `searchState returns to Select when query is cleared after search`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchQuery.edit {
            append("Banana")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            val successState = awaitItem()
            assertTrue(successState is SearchState.Success)
        }
        viewModel.searchQuery.clearText() // Simulates user clearing input
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)
        viewModel.searchState.test {
            awaitItem()
            val finalState = awaitItem()
            assertTrue(
                "Expected Select state, but got $finalState",
                finalState is SearchState.Select,
            )
        }
    }

    @Test
    fun `onSetSearch with Type (checklist) updates search results`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            val checklistType = SearchSort.Type(1) // Index 1 for "Checklist" (note.isCheck == true)
            viewModel.onSetSearch(checklistType)

            val successState = awaitItem()
            assertTrue(successState is SearchState.Success)
            val successResults = (successState as SearchState.Success).searches
            assertEquals(1, successResults.size)
            assertEquals(note1.id, successResults.first().note.id) // note1 is a checklist
            assertEquals(checklistType, successState.searchSort)
        }
    }

    @Test
    fun `onSetSearch with Type (reminder) updates search results`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            val reminderType = SearchSort.Type(0) // Index 0 for "Reminders" (notification != null)
            viewModel.onSetSearch(reminderType)

            val successState = awaitItem()
            assertTrue(successState is SearchState.Success)
            val successResults = (successState as SearchState.Success).searches
            assertEquals(1, successResults.size)
            assertEquals(note4Reminder.id, successResults.first().note.id)
            assertEquals(reminderType, successState.searchSort)
        }
    }

    @Test
    fun `onSetSearch with Label updates search results`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            val workLabelSort =
                SearchSort.Label(name = labelWork.name, iconIndex = 6, id = labelWork.id!!)
            viewModel.onSetSearch(workLabelSort)

            val successState = awaitItem()
            assertTrue(successState is SearchState.Success)
            val successResults = (successState as SearchState.Success).searches
            assertEquals(2, successResults.size) // note1 and note3 have "Work" label
            assertTrue(successResults.any { it.note.id == note1.id })
            assertTrue(successResults.any { it.note.id == note3.id })
            assertEquals(workLabelSort, successState.searchSort)
        }
    }

    @Test
    fun `onSetSearch with Color updates search results`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            val colorSort = SearchSort.Color(colorIndex = 0)
            viewModel.onSetSearch(colorSort)

            val successState = awaitItem()
            assertTrue(successState is SearchState.Success)
            val successResults = (successState as SearchState.Success).searches
            assertEquals(2, successResults.size) // note1 and note3 have color 0
            assertTrue(successResults.any { it.note.id == note1.id })
            assertTrue(successResults.any { it.note.id == note3.id })
            assertEquals(colorSort, successState.searchSort)
        }
    }

    @Test
    fun `onSetSearch with query and then Type updates search results`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)

        viewModel.searchQuery.setTextAndPlaceCursorAtEnd("Content")
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)

        viewModel.searchState.test {
            awaitItem() // Initial Select

            val querySuccessState = awaitItem() as SearchState.Success
            // All 4 notes have "Content" in their content field as per sample data setup.
            assertEquals(4, querySuccessState.searches.size)

            val checklistType = SearchSort.Type(1) // isCheck = true
            viewModel.onSetSearch(checklistType)

            val finalSuccessState = awaitItem()
            assertTrue(finalSuccessState is SearchState.Success)
            val finalResults = (finalSuccessState as SearchState.Success).searches
            assertEquals(1, finalResults.size) // Only note1 is a checklist
            assertEquals(note1.id, finalResults.first().note.id)
            assertEquals(checklistType, finalSuccessState.searchSort)
        }
    }

    @Test
    fun `onSetSearch with Type and then query updates search results`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)

        viewModel.searchState.test {
            awaitItem() // Initial Select

            val checklistType = SearchSort.Type(1) // isCheck = true
            viewModel.onSetSearch(checklistType)

            val typeSuccessState = awaitItem() as SearchState.Success
            assertEquals(1, typeSuccessState.searches.size) // note1
            assertEquals(note1.id, typeSuccessState.searches.first().note.id)
        }
        viewModel.searchQuery.setTextAndPlaceCursorAtEnd("Apple") // note1 has "Apple"
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)

        viewModel.searchState.test {
            val checklistType = SearchSort.Type(1) // isCheck = true
            val finalSuccessState = awaitItem()
            assertTrue(finalSuccessState is SearchState.Success)
            val finalResults = (finalSuccessState as SearchState.Success).searches
            assertEquals(1, finalResults.size)
            assertEquals(note1.id, finalResults.first().note.id)
            assertEquals(checklistType, finalSuccessState.searchSort)
        }
    }

    @Test
    fun `clearing query after onSetSearch with Type resets to Select state`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)

        viewModel.searchState.test {
            awaitItem() // Initial Select

            val checklistType = SearchSort.Type(1)
            viewModel.onSetSearch(checklistType)
            val typeSuccessState = awaitItem() as SearchState.Success // note1
        }
        viewModel.searchQuery.edit {
            append("SomeTextNotInNote1")
        }
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)

        viewModel.searchState.test {
            awaitItem()
            val queryAndTypeSuccessState = awaitItem() as SearchState.Success

            assertEquals(0, queryAndTypeSuccessState.searches.size) // No match
        }
        viewModel.searchQuery.clearText()
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)

        viewModel.searchState.test {
            skipItems(2)
            val finalState = awaitItem()
            assertTrue(
                "Expected Select state after clearing query post-sort, but got $finalState",
                finalState is SearchState.Select,
            )
        }
    }

    @Test
    fun `onSetSearch with null resets to Select state if query is blank`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            viewModel.onSetSearch(SearchSort.Type(0)) // Apply a sort
            val sortedState = awaitItem()
            assertTrue(sortedState is SearchState.Success)

            viewModel.onSetSearch(null) // Reset sort while query is blank
            val finalState = awaitItem()
            assertTrue(
                "Expected Select state but got $finalState",
                finalState is SearchState.Select,
            )
        }
    }

    @Test
    fun `onSetSearch with null keeps Success state if query is NOT blank`() = runTest {
        noteRepository.upserts(sampleNoteMinsFromRepo)
        userDataRepository.setUserData(defaultUserData)
        viewModel.searchQuery.setTextAndPlaceCursorAtEnd("Title") // Matches all 4 notes initially
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(300)
        viewModel.searchState.test {
            awaitItem() // Initial Select

            val querySuccessState = awaitItem() as SearchState.Success
            assertEquals(4, querySuccessState.searches.size)

            viewModel.onSetSearch(SearchSort.Type(1)) // Filter to checklist (note1)
            val sortedSuccessState = awaitItem() as SearchState.Success
            assertEquals(1, sortedSuccessState.searches.size)
            assertEquals(note1.id, sortedSuccessState.searches.first().note.id)

            viewModel.onSetSearch(null) // Remove type sort, keep "Title" query
            val queryOnlySuccessState = awaitItem() as SearchState.Success
            assertEquals(
                4,
                queryOnlySuccessState.searches.size,
            ) // Back to matching "Title" from all notes
            assertNull(queryOnlySuccessState.searchSort)
        }
    }
}
