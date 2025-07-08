package com.mshdabiola.gallery

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.mshdabiola.model.NoteImage
import com.mshdabiola.ui.PreviewContainer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalSharedTransitionApi::class)
class GalleryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private var onBackCalled = false
    private var onToTextWithPath: String? = null
    private var onSendCalled = false
    private var onCopyCalled = false
    private var onDeleteCalled = false

    private val sampleImages = listOf(
        NoteImage(id = 1, path = "path/to/image1.jpg"),
        NoteImage(id = 2, path = "path/to/image2.png"),
        NoteImage(id = 3, path = "path/to/image3.jpeg"),
    )

    private fun setupGalleryScreen(
        images: List<NoteImage> = sampleImages,
        initialPage: Int = 0,
    ) {
        onBackCalled = false
        onToTextWithPath = null
        onSendCalled = false
        onCopyCalled = false
        onDeleteCalled = false

        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(visible = true) {
                    PreviewContainer { // Assuming PreviewContainer sets up theme and necessary scopes
                        val pagerState = rememberPagerState(
                            initialPage = initialPage,
                            pageCount = { images.size },
                        )
                        GalleryScreen(
                            modifier = Modifier.semantics {
                                testTagsAsResourceId = true
                            },
                            galleryUiState = GalleryUiState(images = images),
                            pagerState = pagerState,
                            onBack = { onBackCalled = true },
                            onToText = { path -> onToTextWithPath = path },
                            onSend = { onSendCalled = true },
                            onCopy = { onCopyCalled = true },
                            delete = { onDeleteCalled = true },
                        )
                    }
                }
            }
        }
    }

    @Test
    fun initialElements_areDisplayed_firstPage() {
        setupGalleryScreen(initialPage = 0)
        composeTestRule.onNodeWithTag("gallery:back_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:title").assertTextEquals("1 of ${sampleImages.size}")
        composeTestRule.onNodeWithTag("gallery:more_options_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:pager").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:image_0").assertIsDisplayed()
    }

    @Test
    fun initialElements_areDisplayed_secondPage() {
        setupGalleryScreen(initialPage = 1)
        composeTestRule.onNodeWithTag("gallery:title").assertTextEquals("2 of ${sampleImages.size}")
        composeTestRule.onNodeWithTag("gallery:image_1").assertIsDisplayed()
    }

    @Test
    fun backButton_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag("gallery:back_button").performClick()
        assertTrue(onBackCalled)
    }

    @Test
    fun moreOptionsButton_opensMenu_andItemsAreDisplayed() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag("gallery:more_options_button").performClick()
        composeTestRule.onNodeWithTag("gallery:grab_text_menu_item").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:copy_menu_item").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:send_menu_item").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:delete_menu_item").assertIsDisplayed()
    }

    @Test
    fun grabTextMenuItem_invokesCallback_withCorrectPath() {
        setupGalleryScreen(initialPage = 0)
        composeTestRule.onNodeWithTag("gallery:more_options_button").performClick()
        composeTestRule.onNodeWithTag("gallery:grab_text_menu_item").performClick()
        assertEquals(sampleImages[0].path, onToTextWithPath)
    }

    @Test
    fun copyMenuItem_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag("gallery:more_options_button").performClick()
        composeTestRule.onNodeWithTag("gallery:copy_menu_item").performClick()
        assertTrue(onCopyCalled)
    }

    @Test
    fun sendMenuItem_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag("gallery:more_options_button").performClick()
        composeTestRule.onNodeWithTag("gallery:send_menu_item").performClick()
        assertTrue(onSendCalled)
    }

    @Test
    fun deleteMenuItem_invokesCallback() {
        setupGalleryScreen()
        composeTestRule.onNodeWithTag("gallery:more_options_button").performClick()
        composeTestRule.onNodeWithTag("gallery:delete_menu_item").performClick()
        assertTrue(onDeleteCalled)
    }

    @Test
    fun pagerSwipe_updatesTitleAndImage_andGrabTextPath() {
        setupGalleryScreen(images = sampleImages, initialPage = 0)
        composeTestRule.onNodeWithTag("gallery:image_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:title").assertTextEquals("1 of ${sampleImages.size}")

        composeTestRule.onNodeWithTag("gallery:pager").performScrollToIndex(1)
        composeTestRule.mainClock.advanceTimeByFrame() // Allow recomposition

        composeTestRule.onNodeWithTag("gallery:image_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery:title").assertTextEquals("2 of ${sampleImages.size}")

        // Verify grab text path after swipe
        composeTestRule.onNodeWithTag("gallery:more_options_button").performClick()
        composeTestRule.onNodeWithTag("gallery:grab_text_menu_item").performClick()
        assertEquals(sampleImages[1].path, onToTextWithPath)
    }

    @Composable
    fun GetString(id: Int) = stringResource(id = id)
}
