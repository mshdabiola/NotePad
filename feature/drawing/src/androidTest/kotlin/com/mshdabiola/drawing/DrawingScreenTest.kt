package com.mshdabiola.drawing

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.DrawingPath
import com.mshdabiola.ui.DrawingController
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DrawingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Callbacks verification
    private var onBackClicked = false
    private var onDeleteImageClicked = false
    private var onCopyClicked = false
    private var onSendClicked = false

    private lateinit var fakeDrawingController: FakeDrawingController

    private fun setupScreen(
        drawingUiState: DrawingUiState = DrawingUiState(),
        drawingController: DrawingController = FakeDrawingController(),
    ) {
        onBackClicked = false
        onDeleteImageClicked = false
        onCopyClicked = false
        onSendClicked = false
        fakeDrawingController = drawingController as FakeDrawingController

        composeTestRule.setContent {
            DrawingScreen(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                onBackk = { onBackClicked = true },
                controller = drawingController,
                drawingUiState = drawingUiState,
                onDeleteImage = { onDeleteImageClicked = true },
                onCopy = { onCopyClicked = true },
                onSend = { onSendClicked = true },
            )
        }
    }

    // Fake controller for testing
    class FakeDrawingController :
        DrawingController() {
        var undoCalled = false
        var redoCalled = false

        override fun undo() {
            undoCalled = true
            // Simulate state change for canUndo/canRedo if needed for complex tests
            canUndo = false
            canRedo = true
        }

        override fun redo() {
            redoCalled = true
            // Simulate state change
            canRedo = false
            canUndo = true
        }
    }

    @Test
    fun initialElements_areDisplayed() {
        setupScreen()
        composeTestRule.onNodeWithTag("drawing:back_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("drawing:title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("drawing:undo_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("drawing:redo_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("drawing:more_options_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("drawing:drawing_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("drawing:board").assertIsDisplayed()
    }

    @Test
    fun backButton_invokesCallback() {
        setupScreen()
        composeTestRule.onNodeWithTag("drawing:back_button").performClick()
        assertTrue(onBackClicked)
    }

    @Test
    fun undoButton_whenCanUndo_invokesControllerUndo() {
        val controller = FakeDrawingController().apply { canUndo = true }
        setupScreen(drawingController = controller)

        composeTestRule.onNodeWithTag("drawing:undo_button").assertIsEnabled().performClick()
        assertTrue(controller.undoCalled)
    }

    @Test
    fun undoButton_whenCannotUndo_isDisabled() {
        val controller = FakeDrawingController().apply { canUndo = false }
        setupScreen(drawingController = controller)
        composeTestRule.onNodeWithTag("drawing:undo_button").assertIsNotEnabled()
    }

    @Test
    fun redoButton_whenCanRedo_invokesControllerRedo() {
        val controller = FakeDrawingController().apply { canRedo = true }
        setupScreen(drawingController = controller)

        composeTestRule.onNodeWithTag("drawing:redo_button").assertIsEnabled().performClick()
        assertTrue(controller.redoCalled)
    }

    @Test
    fun redoButton_whenCannotRedo_isDisabled() {
        val controller = FakeDrawingController().apply { canRedo = false }
        setupScreen(drawingController = controller)
        composeTestRule.onNodeWithTag("drawing:redo_button").assertIsNotEnabled()
    }

    @Test
    fun moreOptionsButton_whenDrawingsExist_isEnabledAndOpensMenu_thenMenuItemsWork() {
        setupScreen(
            drawingUiState = DrawingUiState(
                drawings = listOf(DrawingPath()),
            ),
        ) // Provide a NON-EMPTY list

        composeTestRule.onNodeWithTag("drawing:more_options_button")
            .assertIsEnabled() // Crucially, assert it's enabled now
            .performClick()

        // It's good practice to wait a bit for the menu to appear, especially if there are animations.
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag("drawing:copy_menu_item", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verify menu items are displayed and clickable
        composeTestRule.onNodeWithTag("drawing:copy_menu_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(onCopyClicked)
        assertFalse(onSendClicked) // Ensure others weren't called
        assertFalse(onDeleteImageClicked)
        composeTestRule.waitForIdle() // Wait for menu to close

        // Re-open menu for next item
        composeTestRule.onNodeWithTag("drawing:more_options_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag("drawing:send_menu_item", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("drawing:send_menu_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(onSendClicked)
        composeTestRule.waitForIdle() // Wait for menu to close

        composeTestRule.onNodeWithTag("drawing:more_options_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithTag("drawing:delete_menu_item", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("drawing:delete_menu_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(onDeleteImageClicked)
    }

    @Test
    fun moreOptionsButton_whenNoDrawings_isDisabled() {
        setupScreen(drawingUiState = DrawingUiState(drawings = emptyList()))
        composeTestRule.onNodeWithTag("drawing:more_options_button").assertIsNotEnabled()
    }
}
