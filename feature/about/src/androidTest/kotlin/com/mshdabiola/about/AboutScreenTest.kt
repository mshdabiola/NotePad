/*
 *abiola 2022
 */

package com.mshdabiola.about

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class AboutScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun back_button_clickable() {
        composeTestRule.setContent {
            AboutScreen(
                lastUpdate = "12-20-20025",
                version = "2.4.3",
            )
        }

        composeTestRule
            .onNodeWithTag("about:back")
            .assertHasClickAction()
    }
}
