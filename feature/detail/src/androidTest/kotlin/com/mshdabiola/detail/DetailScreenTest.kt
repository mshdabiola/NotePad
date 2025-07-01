/*
 *abiola 2022
 */

package com.mshdabiola.detail

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun loading_showsLoadingSpinner() {
        composeTestRule.setContent {
            SharedTransitionLayout(
                content = {
                    AnimatedVisibility(true) {
                        EditScreen(
                            state = DetailState(
                                notePad = NotePad(
                                    note = Note(),
                                    images = listOf(
                                        NoteImage(1, 2, ""),
                                    ),
                                    drawings = listOf(
                                        NoteDrawing(1, 2),
                                    ),
                                    labels = listOf(
                                        Label(1, "label"),
                                        Label(2, "label2"),
                                    ),
                                    uris = listOf(
                                        NoteUri(1, "", "Path", "akdkdk"),
                                    ),
                                    voices = listOf(
                                        NoteVoice(1, 2, ""),
                                    ),
//                        notification = NotificationUiState(
//                            currentPlace = NotificationPlace.Work,
//                            currentDateTime = LocalDateTime(2025,2,25,12,60,1),
//                            currentInterval = NotificationInterval.Daily(intervalEnd = IntervalEnd.Forever)
//                        )

                                ),
                                title = rememberTextFieldState("Title"),
                                detail = rememberTextFieldState("Detail"),
                            ),
                            animatedContentScope = this,
                        )
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithTag("detail:content", true)
            .assertExists()

        composeTestRule
            .onNodeWithTag("detail:title", true)
            .assertExists()
    }
}
