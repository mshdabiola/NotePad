/*
 *abiola 2023
 */

package com.mshdabiola.detail

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import com.mshdabiola.model.Label
import com.mshdabiola.model.Note
import com.mshdabiola.model.NoteDrawing
import com.mshdabiola.model.NoteImage
import com.mshdabiola.model.NotePad
import com.mshdabiola.model.NoteUri
import com.mshdabiola.model.NoteVoice
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.ui.PreviewMain

class DetailScreenScreenshotTests {

    @PreviewMain
    @Composable
    private fun Main() {
        PreviewContainer {
            DetailScreen(
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
            )
        }
    }

    @PreviewMain
    @Composable
    private fun MainCheck() {
        PreviewContainer {
            DetailScreen(
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
            )
        }
    }
}
