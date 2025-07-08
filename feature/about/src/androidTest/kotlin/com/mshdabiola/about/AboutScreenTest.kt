/*
 *abiola 2022
 */

package com.mshdabiola.about

import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import com.mshdabiola.designsystem.R as Rd

class AboutScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val testVersion = "1.0.0-test"
    private val testLastUpdate = "2023-10-27"

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun back_button_clickable() {
        composeTestRule.setContent {
            AboutScreen(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                lastUpdate = testLastUpdate,
                version = testVersion,
            )
        }

        composeTestRule
            .onNodeWithTag("about:back")
            .assertHasClickAction()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun aboutScreen_displaysAllElementsCorrectly() {
        var expectedTitle = ""
        var expectedAppName = ""
        var expectedAboutMe = ""
        var expectedTerms = ""

        composeTestRule.setContent {
            // Capture string resources within Composable context
            expectedTitle = stringResource(Rd.string.modules_designsystem_about)
            expectedAppName = stringResource(Rd.string.modules_designsystem_play_notepad)
            expectedAboutMe = stringResource(Rd.string.modules_designsystem_about_me)
            expectedTerms = stringResource(Rd.string.modules_designsystem_terms_and_condition)

            AboutScreen(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
                lastUpdate = testLastUpdate,
                version = testVersion,
            )
        }

        // Verify title
        composeTestRule.onNodeWithTag("about:title")
            .assertIsDisplayed()
            .assertTextEquals(expectedTitle)

        // Verify main content column
        composeTestRule.onNodeWithTag("about:content_column")
            .assertIsDisplayed()

        // Verify app name
        composeTestRule.onNodeWithTag("about:app_name")
            .assertIsDisplayed()
            .assertTextEquals(expectedAppName)

        // Verify version value
        composeTestRule.onNodeWithTag("about:version_value")
            .assertIsDisplayed()
            .assertTextEquals(testVersion)

        // Verify last update value
        composeTestRule.onNodeWithTag("about:last_update_value")
            .assertIsDisplayed()
            .assertTextEquals(testLastUpdate)

        // Verify about me text
        composeTestRule.onNodeWithTag("about:about_me")
            .assertIsDisplayed()
            .assertTextEquals(expectedAboutMe)

        // Verify terms and condition text
        composeTestRule.onNodeWithTag("about:terms")
            .assertIsDisplayed()
            .assertTextEquals(expectedTerms)
    }
}
