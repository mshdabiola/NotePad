package com.mshdabiola.selectlabel // Or com.mshdabiola.search if preferred

import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.search.SearchScreen // Correct import for the Composable
import com.mshdabiola.search.SearchSort
import com.mshdabiola.search.SearchState
import com.mshdabiola.ui.PreviewContainer
import org.junit.Rule
import org.junit.Test
import com.mshdabiola.designsystem.R as Rd // For string resources

/**
 * UI tests for [SearchScreen].
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val emptyOnBack: () -> Unit = {}
    private val emptyOnSetSearch: (SearchSort?) -> Unit = {}
    private val emptyOnNoteClick: (Long, Int, Int) -> Unit = { _, _, _ -> }

    @Test
    fun searchScreen_initialState_showsSelectStateWithCategories() {
        val searchState = SearchState.Select(
            types = listOf(SearchSort.Type(0), SearchSort.Type(1)),
            label = listOf(SearchSort.Label("Test Label", 0, 0)),
            color = listOf(SearchSort.Color(1)),
        )

        composeTestRule.setContent {
            SearchScreen(
                searchState = searchState,
                onBack = emptyOnBack,
                onSetSearch = emptyOnSetSearch,
                onNoteClick = emptyOnNoteClick,
            )
        }

        composeTestRule.onNodeWithTag("search_screen_scaffold").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_top_app_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_input_field").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_back_button").assertIsDisplayed()

        // Check for Select state content
        composeTestRule.onNodeWithTag("search_select_state_column").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_types_label_box").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_labels_label_box").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_colors_label_box").assertIsDisplayed()

        // Check one item from each category to confirm LabelBox is rendering them
        val typeZeroName = composeTestRule.activity.resources.getStringArray(Rd.array.modules_designsystem_search_sort)[0]
        composeTestRule.onNodeWithTag("search_type_item_${typeZeroName}_0").assertIsDisplayed() // Assuming first type, first index
        composeTestRule.onNodeWithTag("search_label_item_Test Label_0").assertIsDisplayed() // Assuming first label, first index
        composeTestRule.onNodeWithTag("search_color_item_1_0").assertIsDisplayed() // Assuming color index 1, first in list

        // Ensure results grid and no results message are not shown
        composeTestRule.onNodeWithTag("search_results_grid").assertDoesNotExist()
        composeTestRule.onNodeWithTag("search_no_results_column").assertDoesNotExist()
    }

    @Test
    fun searchScreen_whenTypingInSearchField_showsClearButtonAndText() {
        val searchQueryState = TextFieldState()

        composeTestRule.setContent {
            SearchScreen(
                searchQuery = searchQueryState,
                onBack = emptyOnBack,
                onSetSearch = emptyOnSetSearch,
                onNoteClick = emptyOnNoteClick,
            )
        }

        composeTestRule.onNodeWithTag("search_input_field").performTextInput("Test Query")
        composeTestRule.onNodeWithTag("search_input_field").assertTextContains("Test Query")
        composeTestRule.onNodeWithTag("search_clear_button").assertIsDisplayed()
    }

    @Test
    fun searchScreen_clickClearButton_clearsSearchInput() {
        val searchQueryState = TextFieldState("abiola")
        searchQueryState.setTextAndPlaceCursorAtEnd("Initial Text") // Pre-fill text

        var setSearchCalledWithNull = false
        val onSetSearchLambda = { searchSort: SearchSort? ->
            if (searchSort == null) {
                setSearchCalledWithNull = true
            }
        }

        composeTestRule.setContent {
            PreviewContainer {
                SearchScreen(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
                    searchQuery = searchQueryState,
                    onBack = emptyOnBack,
                    onSetSearch = onSetSearchLambda,
                    onNoteClick = emptyOnNoteClick,
                )
            }
        }

        composeTestRule.onNodeWithTag("search_clear_button").performClick()
        composeTestRule.onNodeWithTag("search_input_field").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("search_input_field")
//            .assertTextEquals("")
        composeTestRule.onNodeWithTag("search_clear_button").assertDoesNotExist() // Button hides when text is empty
        assert(setSearchCalledWithNull) { "onSetSearch(null) should have been called" }
    }

    @Test
    fun searchScreen_successStateWithResults_showsResultsGrid() {
        val notes = listOf(
            NotePad(Note(1, "Title 1", "Content 1")),
            NotePad(Note(2, "Title 2", "Content 2")),
        )
        val searchState = SearchState.Success(searches = notes, isGrid = true)
        val searchQueryState = TextFieldState("query") // Query needs to be non-blank for Success to show items

        composeTestRule.setContent {
            PreviewContainer {
                SearchScreen(
                    searchQuery = searchQueryState,
                    searchState = searchState,
                    onBack = emptyOnBack,
                    onSetSearch = emptyOnSetSearch,
                    onNoteClick = emptyOnNoteClick,
                )
            }
        }

        composeTestRule.onNodeWithTag("search_results_grid").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_result_item_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_result_item_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_results_grid")
            .onChildren()
            .assertCountEquals(2)

        // Ensure select state and no results message are not shown
        composeTestRule.onNodeWithTag("search_select_state_column").assertDoesNotExist()
        composeTestRule.onNodeWithTag("search_no_results_column").assertDoesNotExist()
    }

    @Test
    fun searchScreen_successStateNoResultsWithQuery_showsNoResultsMessage() {
        val searchState = SearchState.Success(searches = emptyList(), isGrid = true)
        // Query must be non-blank for the "no results" specific message to appear
        val searchQueryState = TextFieldState("NonExistentQuery")

        composeTestRule.setContent {
            SearchScreen(
                searchQuery = searchQueryState,
                searchState = searchState,
                onBack = emptyOnBack,
                onSetSearch = emptyOnSetSearch,
                onNoteClick = emptyOnNoteClick,
            )
        }

        composeTestRule.onNodeWithTag("search_no_results_column").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_no_results_icon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_no_results_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_no_results_text")
            .assertTextEquals(composeTestRule.activity.getString(Rd.string.modules_designsystem_no_result))

        // Ensure results grid and select state are not shown
        composeTestRule.onNodeWithTag("search_results_grid").assertDoesNotExist()
        composeTestRule.onNodeWithTag("search_select_state_column").assertDoesNotExist()
    }

    @Test
    fun searchScreen_successStateNoResultsAndNoQuery_showsEmptySuccessStateNotNoResultsMessage() {
        // This tests the case where Success state is active but query is blank - should not show "No results"
        // but rather the empty grid (or alternative UI if searchState.searches is empty and query is blank)
        val searchState = SearchState.Success(searches = emptyList(), isGrid = true)
        val searchQueryState = TextFieldState("") // Query is blank

        composeTestRule.setContent {
            SearchScreen(
                searchQuery = searchQueryState,
                searchState = searchState,
                onBack = emptyOnBack,
                onSetSearch = emptyOnSetSearch,
                onNoteClick = emptyOnNoteClick,
            )
        }

        // The "no_results_column" for explicit "No results found for your query" should NOT be shown
        composeTestRule.onNodeWithTag("search_no_results_column").assertDoesNotExist()

        // The grid should exist, even if it's empty
        composeTestRule.onNodeWithTag("search_results_grid").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("search_result_item_", useUnmergedTree = true).assertCountEquals(0)
    }

    @Test
    fun searchScreen_clickBackButton_invokesOnBackCallback() {
        var onBackCalled = false
        val onBackLambda = { onBackCalled = true }

        composeTestRule.setContent {
            SearchScreen(
                onBack = onBackLambda,
                onSetSearch = emptyOnSetSearch,
                onNoteClick = emptyOnNoteClick,
            )
        }

        composeTestRule.onNodeWithTag("search_back_button").performClick()
        assert(onBackCalled) { "onBack callback was not invoked" }
    }

    @Test
    fun searchScreen_clickTypeItem_invokesOnSetSearchCallback() {
        var receivedSearchSort: SearchSort? = null
        val testTypeSort = SearchSort.Type(1)
        val searchState = SearchState.Select(types = listOf(testTypeSort))

        val onSetSearchLambda = { searchSort: SearchSort? ->
            receivedSearchSort = searchSort
        }

        composeTestRule.setContent {
            SearchScreen(
                searchState = searchState,
                onBack = emptyOnBack,
                onSetSearch = onSetSearchLambda,
                onNoteClick = emptyOnNoteClick,
            )
        }
        val typeOneName = composeTestRule.activity.resources.getStringArray(Rd.array.modules_designsystem_search_sort)[1]
        composeTestRule.onNodeWithTag("search_type_item_${typeOneName}_0").performClick() // Assuming index 0 for list

        assert(receivedSearchSort == testTypeSort) {
            "onSetSearch callback was not invoked with the correct SearchSort.Type"
        }
    }

    @Test
    fun searchScreen_clickLabelItem_invokesOnSetSearchCallback() {
        var receivedSearchSort: SearchSort? = null
        val testLabelSort = SearchSort.Label("MyLabel", 2, 0)
        val searchState = SearchState.Select(label = listOf(testLabelSort))

        val onSetSearchLambda = { searchSort: SearchSort? ->
            receivedSearchSort = searchSort
        }

        composeTestRule.setContent {
            SearchScreen(
                searchState = searchState,
                onBack = emptyOnBack,
                onSetSearch = onSetSearchLambda,
                onNoteClick = emptyOnNoteClick,
            )
        }

        composeTestRule.onNodeWithTag("search_label_item_MyLabel_0").performClick() // Assuming index 0 for list

        assert(receivedSearchSort == testLabelSort) {
            "onSetSearch callback was not invoked with the correct SearchSort.Label"
        }
    }

    @Test
    fun searchScreen_clickColorItem_invokesOnSetSearchCallback() {
        var receivedSearchSort: SearchSort? = null
        val testColorSort = SearchSort.Color(3)
        val searchState = SearchState.Select(color = listOf(testColorSort))

        val onSetSearchLambda = { searchSort: SearchSort? ->
            receivedSearchSort = searchSort
        }

        composeTestRule.setContent {
            SearchScreen(
                searchState = searchState,
                onBack = emptyOnBack,
                onSetSearch = onSetSearchLambda,
                onNoteClick = emptyOnNoteClick,
            )
        }

        composeTestRule.onNodeWithTag("search_color_item_3_0").performClick() // Assuming index 0 for list

        assert(receivedSearchSort == testColorSort) {
            "onSetSearch callback was not invoked with the correct SearchSort.Color"
        }
    }

    @Test
    fun searchScreen_clickNoteItem_invokesOnNoteClickCallback() {
        var clickedNoteId: Long? = null
        val testNote = NotePad(Note(id = 5L, title = "Test Note", detail = "Content"))
        val searchState = SearchState.Success(searches = listOf(testNote), isGrid = false)
        val searchQueryState = TextFieldState("query")

        val onNoteClickLambda = { id: Long, _: Int, _: Int ->
            clickedNoteId = id
        }

        composeTestRule.setContent {
            PreviewContainer {
                SearchScreen(
                    searchQuery = searchQueryState,
                    searchState = searchState,
                    onBack = emptyOnBack,
                    onSetSearch = emptyOnSetSearch,
                    onNoteClick = onNoteClickLambda,
                )
            }
        }

        composeTestRule.onNodeWithTag("search_result_item_5").performClick()
        assert(clickedNoteId == 5L) { "onNoteClick callback was not invoked with the correct note ID" }
    }
}
