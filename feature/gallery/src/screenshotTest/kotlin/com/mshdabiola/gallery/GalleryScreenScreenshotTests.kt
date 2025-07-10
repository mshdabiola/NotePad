/*
 *abiola 2023
 */

package com.mshdabiola.gallery

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import com.mshdabiola.model.NoteImage
import com.mshdabiola.ui.PreviewContainer
import com.mshdabiola.ui.PreviewMain

class GalleryScreenScreenshotTests {
    @OptIn(ExperimentalSharedTransitionApi::class)
    @PreviewMain
    @Composable
    fun Main() {
        PreviewContainer {
            GalleryScreen(
                galleryUiState = GalleryUiState(
                    images = listOf(
                        NoteImage(id = 1),
                    ),

                ),
                pagerState = rememberPagerState(0) { 1 },
            )
        }
    }
}
