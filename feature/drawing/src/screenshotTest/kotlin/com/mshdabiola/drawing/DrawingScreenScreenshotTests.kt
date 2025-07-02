/*
 *abiola 2023
 */

package com.mshdabiola.drawing

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import com.mshdabiola.testing.util.PreviewAllLocales
import com.mshdabiola.ui.PreviewContainer

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewAllLocales
@Composable
private fun DrawingScreenShot() {
    PreviewContainer {
        DrawingScreen()
    }
}
