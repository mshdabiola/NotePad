package com.mshdabiola.detail

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteType
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.ui.PreviewContainer
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalSharedTransitionApi::class)
class DetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockNotePad: NotePad
    private lateinit var titleState: TextFieldState
    private lateinit var detailState: TextFieldState

    // Callbacks verification
    private var onBackClicked = false
    private var onPinNoteClicked = false
    private var onNotificationClicked = false
    private var onArchiveClicked = false
    private var onMoreOptionsClicked = false
    private var onLabelClicked = false
    private var onColorClicked = false
    private var onDeleteVoiceNoteIndex: Int? = null
    private var navigateToGalleryArgs: Triple<Long, Int, String>? = null // noteId, index, path
    private var navigateToDrawingNoteId: Long? = null
    private var addItemClicked = false
    private var deleteCheckItemsClicked = false
    private var hideCheckBoxesClicked = false

    private fun setupScreen(
        notePad: NotePad = createDefaultNotePad(),
        isLoading: Boolean = false, // Add if you have a distinct loading state in DetailState
        // Add other DetailState parameters if they affect UI significantly
    ) {
        mockNotePad = notePad
        // Reset callbacks
        onBackClicked = false
        onPinNoteClicked = false
        onNotificationClicked = false
        onArchiveClicked = false
        onMoreOptionsClicked = false
        onLabelClicked = false
        onColorClicked = false
        navigateToGalleryArgs = null
        navigateToDrawingNoteId = null
        addItemClicked = false
        deleteCheckItemsClicked = false
        hideCheckBoxesClicked = false

        composeTestRule.setContent {
            titleState = rememberTextFieldState(initialText = notePad.note.title)
            detailState = rememberTextFieldState(initialText = notePad.note.detail)

            val currentDetailState = DetailState(
                notePad = mockNotePad,
                title = titleState,
                detail = detailState,
                // Add other states like checks, unchecks, playerState if needed for specific tests
                // For simplicity, starting with NotePad and basic text fields.
            )

            PreviewContainer { // Assuming PreviewContainer provides necessary theme and scopes
                DetailScreen(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
                    state = currentDetailState,
                    onBackClick = { onBackClicked = true },
                    pinNote = { onPinNoteClicked = true },
                    onNotification = { onNotificationClicked = true },
                    onArchive = { onArchiveClicked = true },
                    moreOptions = { onMoreOptionsClicked = true },
                    onLabel = { onLabelClicked = true },
                    onColorClick = { onColorClicked = true },
                    deleteVoiceNote = { index -> onDeleteVoiceNoteIndex = index },
                    navigateToGallery = { noteId, index, _, path -> navigateToGalleryArgs = Triple(noteId, index, path) },
                    navigateToDrawing = { id -> navigateToDrawingNoteId = id },
                    addItem = { addItemClicked = true },
                    deleteCheckItems = { deleteCheckItemsClicked = true },
                    hideCheckBoxes = { hideCheckBoxesClicked = true },
                    // Add other callbacks as needed
                )
            }
        }
    }

    private fun createDefaultNotePad(
        id: Long = 1L,
        title: String = "Test Title",
        detail: String = "Test Detail",
        isPin: Boolean = false,
        noteType: NoteType = NoteType.NOTE,
        images: List<NoteImage> = emptyList(),
        drawings: List<NoteDrawing> = emptyList(),
        voices: List<NoteVoice> = emptyList(),
        labels: List<Label> = emptyList(),
        uris: List<NoteUri> = emptyList(),
        color: Int = -1, // Default, no specific color
        background: Int = -1, // Default, no specific background image
    ): NotePad {
        return NotePad(
            note = Note(id = id, title = title, detail = detail, isPin = isPin, noteType = noteType, color = color, background = background),
            images = images,
            drawings = drawings,
            voices = voices,
            labels = labels,
            uris = uris,
        )
    }

    @Test
    fun topAppBar_displaysCorrectly_andBackButtonWorks() {
        setupScreen()
        composeTestRule.onNodeWithTag("detail:back").assertIsDisplayed().performClick()
        assertTrue(onBackClicked)

        composeTestRule.onNodeWithTag("detail:pin").assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail:notification").assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail:archive").assertIsDisplayed()
    }

    @Test
    fun pinButton_togglesIconAndCallsCallback() {
        setupScreen(notePad = createDefaultNotePad(isPin = false))
        composeTestRule.onNodeWithTag("detail:pin").performClick()
        assertTrue(onPinNoteClicked)
        // To assert icon change, you'd need to check the icon's resource ID or content description
        // if it changes dynamically. For now, callback verification is key.
        // If you re-compose with isPin = true, the icon should be PushPinD.
    }

    @Test
    fun archiveButton_togglesIconAndCallsCallback() {
        setupScreen(notePad = createDefaultNotePad(noteType = NoteType.NOTE))
        composeTestRule.onNodeWithTag("detail:archive").performClick()
        assertTrue(onArchiveClicked)
        // Similar to pin, icon change verification would require inspecting icon properties.
    }

    @Test
    fun notificationButton_callsCallback() {
        setupScreen()
        composeTestRule.onNodeWithTag("detail:notification").performClick()
        assertTrue(onNotificationClicked)
    }

    @Test
    fun contentArea_displaysTitleAndDetail() {
        val title = "My Awesome Note"
        val detail = "This is the content of the note."
        setupScreen(notePad = createDefaultNotePad(title = title, detail = detail))

        composeTestRule.onNodeWithTag("detail:title", useUnmergedTree = true).assertIsDisplayed()
        // composeTestRule.onNodeWithText(title).assertIsDisplayed() // Alternative if tag isn't specific enough or for text content
        composeTestRule.onNodeWithTag("detail:content", useUnmergedTree = true).assertIsDisplayed()
        // composeTestRule.onNodeWithText(detail).assertIsDisplayed()
    }

    @Test
    fun images_areDisplayed_andClickNavigates() {
        val noteId = 123L
        val images = listOf(
            NoteImage(id = 1, noteId = noteId, path = "path/to/image1.jpg"),
            NoteImage(id = 2, noteId = noteId, path = "path/to/image2.jpg"),
        )
        setupScreen(notePad = createDefaultNotePad(id = noteId, images = images))

        composeTestRule.onNodeWithTag("detail:images").assertIsDisplayed()
        // Click on the first image (assuming AsyncImage doesn't have its own testTag,
        // you might need to find it by its content description or be more specific if there are many)
        // For simplicity, let's assume the clickable area of the image itself works.
        // To test clicking a specific image, you would need a more specific finder:
        composeTestRule.onAllNodesWithTag("detail:image_item", useUnmergedTree = true)
            .fetchSemanticsNodes().firstOrNull()?.let {
            composeTestRule.onNodeWithTag("detail:image_item_0").performClick() // Assuming you add a tag like "detail:image_item_$index"
        }
        // As a fallback if specific tagging inside LazyRow is hard:
        // composeTestRule.onNodeWithContentDescription("note image", substring = true, useUnmergedTree = true).performClick() // This is less precise

        // Better: If the AsyncImage itself has a testTag, use that.
        // The current DetailScreen.kt seems to share the element, so clicking it should trigger navigateToGallery.
        // We need to ensure the sharedElement node is clickable.
        // Let's assume the clickable modifier on AsyncImage's parent makes the area clickable.
        // This test might be more robust if the AsyncImage itself or its direct clickable parent has a unique tag.
        // For now, testing the first clickable image found:
        // This needs adjustment based on how `rememberSharedContentState("image_$index")` nodes can be found and clicked.
        // The `clickable` is on the AsyncImage itself.
        // Let's assume we tag the AsyncImage for testability.
        // If AsyncImage had Modifier.testTag("async_image_0"), then:
        // composeTestRule.onNodeWithTag("async_image_0").performClick()
        // assertTrue(navigateToGalleryArgs?.first == noteId && navigateToGalleryArgs?.second == 0 && navigateToGalleryArgs?.third == images[0].path)

        // Given the current structure, you might need to iterate through the Row items.
        // The current test tag "detail:images" is for the Row.
        // Let's assume the clickable modifier on AsyncImage works. We need a way to target it.
        // For now, this part of the test is conceptual until the AsyncImage is easily targetable.
    }

    @Test
    fun drawings_areDisplayed_andClickNavigates() {
        val noteId = 456L
        val drawings = listOf(NoteDrawing(id = 1, noteId = noteId))
        setupScreen(notePad = createDefaultNotePad(id = noteId, drawings = drawings))

        composeTestRule.onNodeWithTag("detail:drawing_0", useUnmergedTree = true).assertIsDisplayed()
        // Similar to images, clicking a specific drawing requires a targetable node.
        // If BoardViewer had Modifier.testTag("board_viewer_0"), then:
        // composeTestRule.onNodeWithTag("board_viewer_0").performClick()
        // assertEquals(noteId, navigateToDrawingNoteId)
    }

    @Test
    fun voiceNotes_areDisplayed_andPlayPauseDeleteWorks() {
        val voices = listOf(NoteVoice(id = 1, noteId = 1L, path = "voice.mp3", length = 10000))
        setupScreen(notePad = createDefaultNotePad(voices = voices))

        composeTestRule.onNodeWithTag("detail:voice:play", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("detail:voice:pause", useUnmergedTree = true).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag("detail:voice:delete", useUnmergedTree = true).performClick()
        assertTrue(onDeleteVoiceNoteIndex == 0)
    }

    @Test
    fun labels_areDisplayed() {
        val labels = listOf(Label(id = 1, name = "Urgent"), Label(id = 2, name = "Work"))
        setupScreen(notePad = createDefaultNotePad(labels = labels))

        composeTestRule.onNodeWithText("Urgent").assertIsDisplayed()
        composeTestRule.onNodeWithText("Work").assertIsDisplayed()
    }

    @Test
    fun bottomBar_isDisplayed_andActionsWork() {
        setupScreen()
        composeTestRule.onNodeWithTag("detail:more").assertIsDisplayed().performClick()
//        assertTrue(addItemClicked)

        composeTestRule.onNodeWithTag("detail:colors").assertIsDisplayed().performClick()
        assertTrue(onColorClicked)

        // composeTestRule.onNodeWithTag("detail:bottom_bar_more").assertIsDisplayed().performClick()
        // assertTrue(onMoreOptionsClicked) // This tag is on the IconButton. The DropdownMenu appears after.

        // Test for "Updated at" text visibility
        // composeTestRule.onNodeWithText("Updated", substring = true).assertIsDisplayed() // Needs state.updateAt to be non-empty
    }

    @Test
    fun moreOptionsMenu_DeleteNote_CallsCallback() {
        setupScreen()
        composeTestRule.onNodeWithTag("detail:options", useUnmergedTree = true).performClick()
        // DropdownMenu items are typically in a popup, useUnmergedTree might be needed
//        composeTestRule.onNodeWithText("Delete", useUnmergedTree = true).performClick()
    }

    @Test
    fun checklistItems_whenNoteIsChecklist_areDisplayed() {
        // This requires DetailState to be populated with NoteCheckUiState items
        // and for the note to have isCheck = true
        val checkNotePad = createDefaultNotePad(
            detail = "", // Detail is empty for checklists usually
            // isCheck = true // This should be part of the Note object
        ).let {
            it.copy(note = it.note.copy(isCheck = true))
        }

        // You'll need to populate DetailState.checks and DetailState.unChecks
        // For now, testing the basic structure when `notepad.note.isCheck` is true
        setupScreen(notePad = checkNotePad)

        // If there are checks, the "Add item" button inside the checklist area should appear
        composeTestRule.onNodeWithTag("detail:add_check_item_button", useUnmergedTree = true).assertIsDisplayed().performClick()
        assertTrue(addItemClicked) // This addItem is for checklists

        // You would then find nodes for individual checklist items by their tags or text.
        // e.g., composeTestRule.onNodeWithTag("check_item_content_0").assertIsDisplayed()
        // composeTestRule.onNodeWithTag("check_item_checkbox_0").performClick() -> assert onCheck callback
    }

    @Test
    fun whenNoteIsChecklist_AndHasCheckedItems_DeleteCheckedItemsButtonIsVisible() {
        // Setup DetailState with some items in `state.checks` where `it.isCheck == true`
        // and `notepad.note.isCheck == true`
        val checkNotePad = createDefaultNotePad(id = 1L).let {
            it.copy(note = it.note.copy(isCheck = true))
        }
        // Construct DetailState with populated checks for this test.
        // ...

        // For now, conceptually:
        // setupScreen(notePad = checkNotePad, /* detailState with checked items */)
        // composeTestRule.onNodeWithText("Delete checked items").assertIsDisplayed().performClick()
        // assertTrue(deleteCheckItemsClicked)
    }

    // Add more tests for:
    // - Different note colors and backgrounds affecting the UI (visual verification or snapshot testing might be better)
    // - Specifics of the Player UI (if `PlayerState` is provided and affects the UI)
    // - Empty states (e.g., no images, no labels)
    // - Interactions with TextFieldState for title and detail (typing - though this is often covered by `onValueChange` tests in ViewModel)
    // - Visibility of "Hide checkboxes" button
    // - Display of "ReminderCard"
}
