package com.mshdabiola.selectlabel

import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SelectLabelScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // --- Fake callbacks using simple lambdas and state holders ---
    private var onBackCalled = false
    private var lastCheckedIndex: Int? = null
    private var onCreateLabelCalled = false

    private val fakeOnBack: () -> Unit = { onBackCalled = true }
    private val fakeOnCheckClick: (Int) -> Unit = { index -> lastCheckedIndex = index }
    private val fakeOnCreateLabel: () -> Unit = { onCreateLabelCalled = true }
    // --- End of fake callbacks ---

    private val sampleLabels = listOf(
        LabelState(1, "Work", ToggleableState.On),
        LabelState(2, "Personal", ToggleableState.Off),
        LabelState(3, "Urgent", ToggleableState.Indeterminate),
        LabelState(4, "Shopping", ToggleableState.Off),
        LabelState(5, "Ideas", ToggleableState.On),
    )

    // Helper to reset callback states before each test if needed, or manage locally
    private fun resetCallbackStates() {
        onBackCalled = false
        lastCheckedIndex = null
        onCreateLabelCalled = false
    }

    @Test
    fun screen_isDisplayed_withBasicElements() {
        resetCallbackStates()
        val uiState = SelectLabelUiState(
            labels = sampleLabels.take(2),
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectLabelUiState = uiState,
                onBack = fakeOnBack,
                onCheckClick = fakeOnCheckClick,
                onCreateLabel = fakeOnCreateLabel,
            )
        }

        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.TOP_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.LABEL_QUERY_TEXT_FIELD).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.LABEL_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.CREATE_LABEL_BUTTON).assertIsNotDisplayed()
    }

    @Test
    fun backButton_callsOnBack_whenClicked() {
        resetCallbackStates()
        val uiState = SelectLabelUiState(labelQuery = TextFieldState(""))
        composeTestRule.setContent {
            SelectLabelScreen(selectLabelUiState = uiState, onBack = fakeOnBack)
        }

        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.BACK_BUTTON).performClick()
        assertTrue("onBack callback should have been called", onBackCalled)
    }

    @Test
    fun labelQueryTextField_acceptsInput() {
        resetCallbackStates()
        val initialQuery = ""
        val newQuery = "New Query"
        val textFieldState = TextFieldState(initialQuery)
        // Ensure no direct text modification that would break the TextFieldState's internal logic
        // We're simulating user typing

        val uiState = SelectLabelUiState(
            labels = emptyList(),
            labelQuery = textFieldState,
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(selectLabelUiState = uiState)
        }

        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.LABEL_QUERY_TEXT_FIELD)
            .performTextInput(newQuery) // Type the full new query

        // To verify the text field accepted input, we check the TextFieldState's text property.
        // This is safe as `performTextInput` simulates user input which updates the state.
        assertEquals(newQuery, textFieldState.text.toString())
    }

    @Test
    fun createLabelButton_isDisplayed_andCallsOnCreateLabel_whenShowAddLabelIsTrue() {
        resetCallbackStates()
        val queryText = "New Label"
        val textFieldState = TextFieldState()
        // It's better to let performTextInput update the state if testing user interaction
        // but for setting up the state for the button, direct modification is fine.
        textFieldState.setTextAndPlaceCursorAtEnd(queryText)

        val uiState = SelectLabelUiState(
            labels = emptyList(),
            labelQuery = textFieldState,
            showAddLabel = true,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectLabelUiState = uiState,
                onCreateLabel = fakeOnCreateLabel,
            )
        }

        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.CREATE_LABEL_BUTTON)
            .assertIsDisplayed()
            .performClick()

        assertTrue("onCreateLabel callback should have been called", onCreateLabelCalled)
    }

    @Test
    fun labelList_displaysItems_andCheckboxClickCallsOnCheckClick() {
        resetCallbackStates()
        val uiState = SelectLabelUiState(
            labels = sampleLabels,
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectLabelUiState = uiState,
                onCheckClick = fakeOnCheckClick,
            )
        }

        // Check first item's text and checkbox
        val firstLabel = sampleLabels[0]
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemText(firstLabel.id))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(firstLabel.id))
            .assertIsDisplayed()
            .performClick()

        assertEquals("onCheckClick should have been called with index 0", 0, lastCheckedIndex)

        // Scroll to and check last item
        val lastLabel = sampleLabels.last()
        // Ensure you have `import androidx.compose.ui.test.hasTestTag`
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.LABEL_LIST)
            .performScrollToNode(hasTestTag(SelectLabelScreenTestTags.labelItem(lastLabel.id)))

        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemText(lastLabel.id))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(lastLabel.id))
            .assertIsDisplayed()
            .performClick()

        assertEquals("onCheckClick should have been called with the last item's index", sampleLabels.size - 1, lastCheckedIndex)
    }

    @Test
    fun labelItemCheckbox_reflectsToggleableState() {
        resetCallbackStates()
        var clickedIndexForThisTest: Int? = null
        val customFakeOnCheckClick: (Int) -> Unit = { index -> clickedIndexForThisTest = index }

        val labelsWithDifferentStates = listOf(
            LabelState(10, "Is On", ToggleableState.On),
            LabelState(11, "Is Off", ToggleableState.Off),
            LabelState(12, "Is Indeterminate", ToggleableState.Indeterminate),
        )
        val uiState = SelectLabelUiState(
            labels = labelsWithDifferentStates,
            labelQuery = TextFieldState(""),
            showAddLabel = false,
        )
        composeTestRule.setContent {
            SelectLabelScreen(
                selectLabelUiState = uiState,
                onCheckClick = customFakeOnCheckClick, // Use a local fake for this specific test
            )
        }

        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(10L))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(11L))
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(12L))
            .assertIsDisplayed()

        // Click the first checkbox and verify its index
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(10L)).performClick()
        assertEquals("Clicked checkbox for item at index 0", 0, clickedIndexForThisTest)

        // Reset for next click or use separate tests for each item if verifying individual clicks uniquely
        clickedIndexForThisTest = null
        composeTestRule.onNodeWithTag(SelectLabelScreenTestTags.labelItemCheckbox(11L)).performClick()
        assertEquals("Clicked checkbox for item at index 1", 1, clickedIndexForThisTest)
    }
}

// Helper extension if not already available in your test setup
fun TextFieldState.setTextAndPlaceCursorAtEnd(text: String) {
    this.edit {
        this.replace(0, text.length, text)
        // this.selectCharsIn(text.length, text.length) // Cursor placement, sometimes tricky with TextFieldState
    }
}
