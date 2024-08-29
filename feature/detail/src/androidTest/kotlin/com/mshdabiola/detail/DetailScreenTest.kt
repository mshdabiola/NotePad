/*
 *abiola 2022
 */

package com.mshdabiola.detail

import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loading_showsLoadingSpinner() {
        composeTestRule.setContent {
            DetailScreen(
                content = rememberTextFieldState(),
                title = rememberTextFieldState(),
                onShowSnackbar = { _, _ -> false },
            )
        }

        composeTestRule
            .onNodeWithTag("detail:loading")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithTag("detail:content")
            .assertExists()

        composeTestRule
            .onNodeWithTag("detail:title")
            .assertExists()
    }
}
