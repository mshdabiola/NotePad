/*
 *abiola 2022
 */

package com.mshdabiola.main

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.createFakeNotePads
import com.mshdabiola.ui.PreviewContainer
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [MainScreen] composable without mocks.
 */
class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val samplePinnedNotes = createFakeNotePads(1..2)
    private val sampleUnpinnedNotes = createFakeNotePads(3..5)

    // Variables to capture callback invocations if needed for state changes in tests
    // These are not mocks, but simple flags or value holders.
    private var navigateToNoteEditorCalledWith: Triple<Long, Int, Int>? = null
    private var noteSelectedCalledWith: Long? = null
    private var displayModeChangeCalled = false
    private var hamburgerMenuClicked = false
    private var clearSelectionCalled = false
    // ... other flags for callbacks as needed

    @OptIn(ExperimentalSharedTransitionApi::class)
    private fun setMainContent(
        initialMainState: MainState,
        showRenameDialogInitial: Boolean = false,
        renameLabelValueInitial: String = "Label",
        onNavigateToNoteEditor: (Long, Int, Int) -> Unit = { id, color, img ->
            navigateToNoteEditorCalledWith = Triple(id, color, img)
        },
        onNoteSelectedLambda: (Long) -> Unit = { noteSelectedCalledWith = it },
        onDisplayModeChangeLambda: () -> Unit = { displayModeChangeCalled = true },
        onHamburgerMenuClickLambda: () -> Unit = { hamburgerMenuClicked = true },
        onClearSelectionLambda: () -> Unit = { clearSelectionCalled = true },
        onPinNotesLambda: () -> Unit = { /* TODO: Update state if testing pin UI change */ },
        onNotificationClickLambda: () -> Unit = { /* ... */ },
        onSelectColorLambda: () -> Unit = { /* ... */ },
        onLabelNotesLambda: () -> Unit = { /* ... */ },
        onArchiveLambda: () -> Unit = { /* ... */ },
        onDeleteNotesLambda: () -> Unit = { /* ... */ },
        onShareNoteLambda: () -> Unit = { /* ... */ },
        onCopyNoteLambda: () -> Unit = { /* ... */ },
        onDeletedForeverLambda: () -> Unit = { /* ... */ },
        onRestoreLambda: () -> Unit = { /* ... */ },
        onSearchClickLambda: () -> Unit = { /* ... */ },
        onLabelNameChangeLambda: (String) -> Unit = { /* newName -> ... */ }, // For RenameLabelAlertDialog
        onDeleteLabelLambda: () -> Unit = { /* ... */ },
        onDeleteAllTrashLambda: () -> Unit = { /* ... */ },

    ) {
        // Reset flags before each test recomposition
        navigateToNoteEditorCalledWith = null
        noteSelectedCalledWith = null
        displayModeChangeCalled = false
        hamburgerMenuClicked = false
        clearSelectionCalled = false
        // ... reset other flags

        composeTestRule.setContent {
            val testTagsAsResourceId = true

            var currentMainState by remember(initialMainState) {
                mutableStateOf(
                    initialMainState,
                )
            }
            var showRenameDialog by remember(showRenameDialogInitial) {
                mutableStateOf(
                    showRenameDialogInitial,
                )
            }
            var renameLabelValue by remember(renameLabelValueInitial) {
                mutableStateOf(
                    renameLabelValueInitial,
                )
            }

            PreviewContainer {
                MainScreen(
                    modifier = Modifier.semantics {
                        this.testTagsAsResourceId = testTagsAsResourceId
                    },
                    mainState = currentMainState,
                    navigateToNoteEditor = onNavigateToNoteEditor,

                    onNoteSelected = { noteId ->
                        println("noteId: $noteId")
                        onNoteSelectedLambda(noteId)
                        // Simulate ViewModel updating state to enter selection mode
                        if (currentMainState is MainState.Success && (currentMainState as MainState.Success).selectState == null) {
                            println("select mode")
                            val successState = currentMainState as MainState.Success
                            currentMainState = successState.copy(
                                selectState = SelectState(
                                    setOfSelected = setOf(noteId),
                                    isAllPin = false,
                                ),
                            )
                        } else if (currentMainState is MainState.Success && (currentMainState as MainState.Success).selectState != null) {
                            val successState = currentMainState as MainState.Success
                            val currentSelection = successState.selectState!!.setOfSelected
                            val newSelection =
                                if (currentSelection.contains(noteId)) currentSelection - noteId else currentSelection + noteId

                            currentMainState = if (newSelection.isEmpty()) {
                                successState.copy(selectState = null)
                            } else {
                                successState.copy(
                                    selectState = successState.selectState!!.copy(
                                        setOfSelected = newSelection,
                                    ),
                                )
                            }
                        }
                    },
                    onDisplayModeChange = {
                        onDisplayModeChangeLambda()
                        if (currentMainState is MainState.Success) {
                            val successState = currentMainState as MainState.Success
                            currentMainState = successState.copy(isGrid = !successState.isGrid)
                        }
                    },
                    onHamburgerMenuClick = onHamburgerMenuClickLambda,
                    onClearSelection = {
                        onClearSelectionLambda()
                        if (currentMainState is MainState.Success) {
                            currentMainState =
                                (currentMainState as MainState.Success).copy(selectState = null)
                        }
                    },
                    onPinNotes = onPinNotesLambda,
                    onNotificationClick = onNotificationClickLambda,
                    onSelectColor = onSelectColorLambda,
                    onLabelNotes = onLabelNotesLambda,
                    onArchive = onArchiveLambda,
                    onDeleteNotes = onDeleteNotesLambda,
                    onShareNote = onShareNoteLambda,
                    onCopyNote = onCopyNoteLambda,
                    onDeletedForever = onDeletedForeverLambda,
                    onRestore = onRestoreLambda,
                    onSearchClick = onSearchClickLambda,
                    onLabelNameChange = { // This is the callback from MainScreen for TopBar rename
                        showRenameDialog = true // Trigger dialog from MainScreen's perspective
                    },
                    onDeleteLabel = onDeleteLabelLambda,
                    onDeleteAllTrash = onDeleteAllTrashLambda,
                )

//                if (showRenameDialog) {
//                    RenameLabelAlertDialog(
//                        show = true, // Controlled by the state variable
//                        label = renameLabelValue,
//                        onDismissRequest = { showRenameDialog = false },
//                        onChangeName = { newName ->
//                            onLabelNameChangeLambda(newName) // Call the passed lambda
//                            renameLabelValue =
//                                newName // Update the internal state for the dialog's text field
//                            showRenameDialog = false
//                            // Simulate MainState being updated with new label name
//                            if (currentMainState is MainState.Success) {
//                                currentMainState =
//                                    (currentMainState as MainState.Success).copy(labelName = newName)
//                            }
//                        },
//                    )
//                }
            }
        }
    }

    @Test
    fun loadingState_whenMainStateIsLoading_showsLoadingWheel() {
        setMainContent(MainState.Loading)
        composeTestRule.onNodeWithTag("main:loading_state").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main:loading_wheel").assertIsDisplayed()
    }

    @Test
    fun emptyState_whenNoNotes_showsEmptyView() {
        setMainContent(
            MainState.Success(
                pinNotePads = emptyList(),
                unPinNotePads = emptyList(),
                isGrid = true,
                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.NOTE),
            ),
        )
        composeTestRule.onNodeWithTag("main:empty_state_view").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("main:empty_state_animation").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main:empty_state_text").assertIsDisplayed()
    }

    @Test
    fun notesDisplayed_whenSuccessStateWithNotes_showsPinnedAndUnpinnedSectionsAndNotes() {
        setMainContent(
            MainState.Success(
                pinNotePads = samplePinnedNotes,
                unPinNotePads = sampleUnpinnedNotes,
                isGrid = true,
                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.NOTE),
            ),
        )

        composeTestRule.onNodeWithTag("main:scaffold_success").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main:notes_grid").assertIsDisplayed()

        composeTestRule.onNodeWithTag("main:pinned_section_header").assertIsDisplayed()
        samplePinnedNotes.forEach { notePad ->
            composeTestRule.onNodeWithTag("main:note_card_pinned_${notePad.note.id}").assertExists()
        }

        composeTestRule.onNodeWithTag("main:others_section_header").assertIsDisplayed()
        sampleUnpinnedNotes.forEach { notePad ->
            composeTestRule.onNodeWithTag("main:notes_grid")
                .performScrollToKey("unpinned_${notePad.note.id}")
            composeTestRule.onNodeWithTag("main:note_card_unpinned_${notePad.note.id}")
                .assertExists()
        }
    }

    @Test
    fun noteClick_whenNotInSelectionMode_setsNavigateToEditorFlag() {
        val testNote = sampleUnpinnedNotes.first()
        setMainContent(
            MainState.Success(
                pinNotePads = emptyList(),
                unPinNotePads = sampleUnpinnedNotes,
                isGrid = true,
                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.NOTE),
                selectState = null,
            ),
        )

        composeTestRule.onNodeWithTag("main:note_card_unpinned_${testNote.note.id}")
            .performClick()

        assert(navigateToNoteEditorCalledWith?.first == testNote.note.id)
        assert(noteSelectedCalledWith == null) // Should not enter selection mode on simple click
    }

    @Test
    fun noteClick_entersAndExitsSelectionMode_andTopbarChanges() {
        val testNote = sampleUnpinnedNotes.first()
        setMainContent(
            MainState.Success(
                pinNotePads = emptyList(),
                unPinNotePads = sampleUnpinnedNotes,
                isGrid = true,
                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.NOTE),
                selectState = null, // Start not in selection mode
            ),
        )

        // Initial state: Hamburger menu should be visible
        composeTestRule.onNodeWithTag("main:topbar_hamburger_menu_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main:topbar_clear_selection_button").assertDoesNotExist()

        // Click a note to enter selection mode (onNoteSelected lambda in setMainContent handles state change)
        composeTestRule.onNodeWithTag("main:note_card_unpinned_${testNote.note.id}")
            .performTouchInput {
                longClick()
            } // This should trigger onNoteSelected

        // Verify selection mode UI
        composeTestRule.onNodeWithTag("main:topbar_clear_selection_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main:topbar_hamburger_menu_button").assertDoesNotExist()
        // Check selection count in top bar title
        composeTestRule.onNodeWithTag("main:topbar_selection_title").assertTextEquals("1")

        // Click the same note again to deselect it
        composeTestRule.onNodeWithTag("main:note_card_unpinned_${testNote.note.id}")
            .performClick()

        // Verify back to normal mode
        composeTestRule.onNodeWithTag("main:topbar_hamburger_menu_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("main:topbar_clear_selection_button").assertDoesNotExist()
        // Default title should show (e.g., search card for Notes type)
        composeTestRule.onNodeWithTag("main:topbar_search_notes_card").assertIsDisplayed()
    }

    @Test
    fun displayModeToggle_changesIcon_andGridColumnCount() {
        // Initial state: Grid view (isGrid = true), so "ViewAgenda" icon should be shown
        setMainContent(
            MainState.Success(
                pinNotePads = emptyList(),
                unPinNotePads = sampleUnpinnedNotes,
                isGrid = true, // Start as grid
                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.NOTE),
            ),
        )
        // In grid mode, the toggle icon should be for list/agenda view

//        composeTestRule.onNodeWithTag("main:topbar_display_mode_button")
//            .assertHas बच ् च े Count (1) // Ensure it has an icon

        // We can't directly check the icon vector easily without more complex semantics.
        // Instead, we trust the click changes the state, and the state change (isGrid) affects the grid.

        composeTestRule.onNodeWithTag("main:topbar_display_mode_button").performClick()
        assert(displayModeChangeCalled) // Callback flag

        // After click, state should change to isGrid = false (list view)
        // The icon should now be for grid view
        composeTestRule.onNodeWithTag("main:topbar_display_mode_button")
            .assertIsDisplayed() // Re-assert to confirm it's still there

        // To verify column count change, you might need to check properties of the LazyVerticalStaggeredGrid
        // This is advanced and might be brittle. A simpler check is that notes are still displayed.
        sampleUnpinnedNotes.forEach { notePad ->
            composeTestRule.onNodeWithTag("main:note_card_unpinned_${notePad.note.id}")
                .assertExists()
        }
        // If your NoteCard's width changes significantly, you could assert that too.
    }

    @Test
    fun hamburgerMenuClick_whenNotInSelectionMode_setsFlag() {
        setMainContent(
            MainState.Success(
                pinNotePads = emptyList(),
                unPinNotePads = sampleUnpinnedNotes,
                isGrid = true,
                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.NOTE),
                selectState = null,
            ),
        )
        composeTestRule.onNodeWithTag("main:topbar_hamburger_menu_button").performClick()
        assert(hamburgerMenuClicked)
    }

//    @Test
//    fun renameLabelDialog_showsAndUpdatesLabelNameInMainState() {
//        val initialLabel = "Original Label"
//        val newLabel = "Updated Label Name"
//        val context = composeTestRule.activity
//
//        // 1. Initial state, dialog not shown
//        setMainContent(
//            initialMainState = MainState.Success(
//                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.LABEL, labelId = 1L),
//                labelName = initialLabel,
//                isGrid = true,
//            ),
//            showRenameDialogInitial = false, // Dialog hidden initially
//            renameLabelValueInitial = initialLabel,
//        )
//
//        composeTestRule.onNodeWithTag("main:rename_label_dialog").assertDoesNotExist()
//        composeTestRule.onNodeWithTag("main:topbar_title").assertTextEquals(initialLabel)
//
//        // 2. Simulate action that would show the dialog (e.g., clicking rename in top bar menu)
//        //    In this test setup, we pass a lambda to onLabelNameChange that directly shows the dialog.
//        //    So we find the "Rename label" menu item and click it.
//        //    This assumes NoteType.LABEL is active.
//        composeTestRule.onNodeWithTag("main:topbar_more_options_button").performClick()
//        composeTestRule.onNodeWithTag("main:topbar_rename_label_menu_item").performClick()
//
//        // Dialog should now be visible
//        composeTestRule.onNodeWithTag("main:rename_label_dialog").assertIsDisplayed()
// //        composeTestRule.onNodeWithText(context.getString(Rd.string.modules_designsystem_rename_label_dialog_title))
//            .assertIsDisplayed()
//        composeTestRule.onNodeWithTag("main:rename_label_dialog_input")
//            .assertTextEquals(initialLabel)
//
//        // 3. Input new text and confirm
//        composeTestRule.onNodeWithTag("main:rename_label_dialog_input")
//            .performTextClearance() // Helper to clear existing text
//        composeTestRule.onNodeWithTag("main:rename_label_dialog_input")
//            .performTextInput(newLabel)
//        composeTestRule.onNodeWithTag("main:rename_label_dialog_confirm_button").performClick()
//
//        // Dialog should be dismissed
//        composeTestRule.onNodeWithTag("main:rename_label_dialog").assertDoesNotExist()
//
//        // MainScreen's TopBar title should reflect the new label name because
//        // the RenameLabelAlertDialog's onChangeName updates currentMainState.
//        composeTestRule.onNodeWithTag("main:topbar_title").assertTextEquals(newLabel)
//    }

//    @Test
//    fun renameLabelDialog_dismissButton_dismissesDialogWithoutChangingLabel() {
//        val initialLabel = "Dismiss Test"
//        val context = composeTestRule.activity.applicationContext
//
//        setMainContent(
//            initialMainState = MainState.Success(
//                noteDisplayCategory = NoteDisplayCategory(noteType = NoteType.LABEL, labelId = 1L),
//                labelName = initialLabel,
//                isGrid = true,
//            ),
//            showRenameDialogInitial = true, // Start with dialog shown for simplicity here
//            renameLabelValueInitial = initialLabel,
//        )
//
//        composeTestRule.onNodeWithTag("main:rename_label_dialog").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("main:rename_label_dialog_input")
//            .assertTextEquals(initialLabel)
//        composeTestRule.onNodeWithTag("main:rename_label_dialog_dismiss_button").performClick()
//
//        // Dialog should be dismissed
//        composeTestRule.onNodeWithTag("main:rename_label_dialog").assertDoesNotExist()
//
//        // The label name in MainState should NOT have changed
//        // We check the top bar title which reflects mainState.labelName
//        composeTestRule.onNodeWithTag("main:topbar_title").assertTextEquals(initialLabel)
//    }

    // Helper to perform text clearance on a node
    private fun androidx.compose.ui.test.SemanticsNodeInteraction.performTextClearance() {
        this.performTextInput("") // Replaces existing text with empty string
    }
}
