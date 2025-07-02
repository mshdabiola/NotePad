/*
 *abiola 2023
 */

package com.mshdabiola.gallery

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import com.mshdabiola.model.NoteImage
import com.mshdabiola.testing.util.PreviewAllLocales
import com.mshdabiola.ui.PreviewContainer

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
fun GalleryScreenShot() {
    PreviewContainer {
        GalleryScreen(
            galleryUiState = GalleryUiState(
                images = listOf(
                    NoteImage(id = 1),
                    NoteImage(id = 1),
                    NoteImage(id = 1),

                ),

            ),
            pagerState = rememberPagerState(1) { 2 },
        )
    }
}
