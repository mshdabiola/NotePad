/*
 *abiola 2023
 */

package com.mshdabiola.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mshdabiola.common.result.Result
import com.mshdabiola.testing.util.Capture

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun MainScreenShot() {
    SharedTransitionLayout {
        AnimatedVisibility(true) {
            MainScreen(
                mainState = MainState.Success(),
                searchState = TextFieldState(),
                sharedTransitionScope = this@SharedTransitionLayout,
                animatedContentScope = this,
            )
        }

    }
}
