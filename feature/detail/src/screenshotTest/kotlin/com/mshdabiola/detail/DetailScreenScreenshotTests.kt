/*
 *abiola 2023
 */

package com.mshdabiola.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.testing.util.Capture

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun DetailScreenShot() {
    Capture {
        SharedTransitionLayout {
            AnimatedVisibility(true) {
                EditScreen(
                    notepad = com.mshdabiola.model.NotePad(),
                    title = TextFieldState(),
                    content = TextFieldState(),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this,
                )
            }

        }

    }
}
