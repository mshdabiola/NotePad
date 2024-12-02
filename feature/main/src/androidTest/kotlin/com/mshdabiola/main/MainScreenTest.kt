/*
 *abiola 2022
 */

package com.mshdabiola.main

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [MainScreen] composable.
 */
class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun enterText_showsShowText() {
        composeTestRule.setContent {
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

        composeTestRule
            .onNodeWithTag("main:list")
            .assertExists()
    }
}
