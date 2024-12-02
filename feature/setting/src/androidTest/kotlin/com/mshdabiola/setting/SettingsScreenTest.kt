/*
 *abiola 2022
 */

package com.mshdabiola.setting

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [MainScreen] composable.
 */
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun enterText_showsShowText() {
        composeTestRule.setContent {
            SettingScreen(
                settingState = SettingState.Success(),
            )
        }

        composeTestRule.onNodeWithTag("setting:screen").assertExists()
        composeTestRule.onNodeWithTag("setting:theme").assertExists()
        composeTestRule.onNodeWithTag("setting:mode").assertExists()
    }
}
