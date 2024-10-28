/*
 *abiola 2022
 */

package com.mshdabiola.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.mshdabiola.common.result.Result
import com.mshdabiola.ui.NotePreviewParameterProvider
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [MainScreen] composable.
 */
class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    private val notes = NotePreviewParameterProvider().values.first()

    @Test
    fun enterText_showsShowText() {
        composeTestRule.setContent {
            MainScreen(
                mainState = Result.Success(notes),
                onClick = {},
                onShowSnackbar = { _, _ -> false },
            )
        }

        composeTestRule
            .onNodeWithTag("main:list")
            .assertExists()
    }
}
