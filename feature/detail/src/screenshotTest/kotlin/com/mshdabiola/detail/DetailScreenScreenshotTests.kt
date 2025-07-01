/*
 *abiola 2023
 */

package com.mshdabiola.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.mshdabiola.designsystem.theme.NotePadTheme
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.testing.util.PreviewAllLocales

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
private fun DetailScreenShotForNote() {
    NotePadTheme {
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
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
private fun DetailScreenShotForCheck() {
    NotePadTheme {
        SharedTransitionLayout(
            content = {
                AnimatedVisibility(true) {
                    EditScreen(
                        state = DetailState(
                            notePad = NotePad(
                                note = Note(
                                    isCheck = true,
                                ),
                            ),
                            checks = remember {
                                mutableStateListOf(
                                    NoteCheckUiState(
                                        id = 1,
                                        content = TextFieldState("Hello World"),
                                        isCheck = true,
                                        focus = true,

                                    ),
                                    NoteCheckUiState(
                                        id = 2,
                                        content = TextFieldState("Hello World"),
                                        isCheck = true,
                                        focus = false,
                                    ),
                                    NoteCheckUiState(
                                        id = 3,
                                        content = TextFieldState("Hello World"),
                                        isCheck = true,
                                        focus = true,

                                    ),
                                    NoteCheckUiState(
                                        id = 4,
                                        content = TextFieldState("Hello World"),
                                        isCheck = true,
                                        focus = false,
                                    ),

                                )
                            },
                            unChecks = remember {
                                mutableStateListOf(
                                    NoteCheckUiState(
                                        id = 1,
                                        content = TextFieldState("Hello World"),
                                        isCheck = false,
                                        focus = true,

                                    ),
                                    NoteCheckUiState(
                                        id = 2,
                                        content = TextFieldState("Hello World"),
                                        isCheck = false,
                                        focus = false,
                                    ),
                                    NoteCheckUiState(
                                        id = 3,
                                        content = TextFieldState("Hello World"),
                                        isCheck = false,
                                        focus = true,

                                    ),
                                    NoteCheckUiState(
                                        id = 4,
                                        content = TextFieldState("Hello World"),
                                        isCheck = false,
                                        focus = false,
                                    ),

                                )
                            },

                        ),
                        animatedContentScope = this,
                    )
                }
            },
        )
    }
}
