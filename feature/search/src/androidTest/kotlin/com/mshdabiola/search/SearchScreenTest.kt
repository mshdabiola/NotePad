/*
 *abiola 2022
 */

package com.mshdabiola.search

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [MainScreen] composable.
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun enterText_showsShowText() {
        composeTestRule.setContent {
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    MainScreen(
                        modifier = Modifier.semantics {
                            testTagsAsResourceId = true
                        },
                        mainState = SearchState.Success(),
                        searchState = TextFieldState(),
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this,
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithTag("search:list")
            .assertExists()
    }
}
