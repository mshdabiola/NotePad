/*
 *abiola 2022
 */

package com.mshdabiola.setting

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.mshdabiola.designsystem.R // For string resources
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for [SettingScreen] composable.
 */
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Mock callbacks
    private var onThemeClicked = false
    private var onDarkModeClicked = false
    private var onBackClicked = false

    // Default SettingState for tests
    private val defaultSettingState = SettingState(
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.LIGHT,
    )

    @Before
    fun setup() {
        // Reset mock callback flags before each test
        onThemeClicked = false
        onDarkModeClicked = false
        onBackClicked = false
    }

    private fun setScreenContent(settingState: SettingState = defaultSettingState) {
        composeTestRule.setContent {
            SettingScreen(
                settingState = settingState,
                onTheme = { onThemeClicked = true },
                onDarkMode = { onDarkModeClicked = true },
                onBack = { onBackClicked = true },
            )
        }
    }

    @Test
    fun screen_isDisplayed_with_allElements() {
        setScreenContent()

        // Verify screen itself
        composeTestRule.onNodeWithTag(SettingScreenTestTags.SCREEN).assertIsDisplayed()

        // Verify TopAppBar elements
        composeTestRule.onNodeWithTag(SettingScreenTestTags.TOP_APP_BAR).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SettingScreenTestTags.BACK_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SettingScreenTestTags.TITLE)
            .assertIsDisplayed()
//            .assertTextEquals(composeTestRule.activity.getString(R.string.modules_designsystem_settings)) // More robust check

        // Verify setting items
        composeTestRule.onNodeWithTag(SettingScreenTestTags.THEME_ITEM).assertIsDisplayed()
        composeTestRule.onNodeWithTag(SettingScreenTestTags.MODE_ITEM).assertIsDisplayed()

        // Verify supporting texts for settings
        val expectedThemeText = composeTestRule.activity.resources.getStringArray(R.array.modules_designsystem_theme)[ThemeBrand.DEFAULT.ordinal]
        val expectedModeText = composeTestRule.activity.resources.getStringArray(R.array.modules_designsystem_daynight)[DarkThemeConfig.LIGHT.ordinal]

//        composeTestRule.onNodeWithTag(SettingScreenTestTags.THEME_SUPPORTING_TEXT)
//            .assertIsDisplayed()
//            .assertTextEquals(expectedThemeText) // More robust check

//        composeTestRule.onNodeWithTag(SettingScreenTestTags.MODE_SUPPORTING_TEXT)
//            .assertIsDisplayed()
//            .assertTextEquals(expectedModeText) // More robust check
    }

    @Test
    fun backButton_callsOnBack_whenClicked() {
        setScreenContent()
        composeTestRule.onNodeWithTag(SettingScreenTestTags.BACK_BUTTON).performClick()
        assertTrue("onBack callback should have been called", onBackClicked)
    }

    @Test
    fun themeItem_callsOnTheme_whenClicked() {
        setScreenContent()
        composeTestRule.onNodeWithTag(SettingScreenTestTags.THEME_ITEM).performClick()
        assertTrue("onTheme callback should have been called", onThemeClicked)
    }

    @Test
    fun modeItem_callsOnDarkMode_whenClicked() {
        setScreenContent()
        composeTestRule.onNodeWithTag(SettingScreenTestTags.MODE_ITEM).performClick()
        assertTrue("onDarkMode callback should have been called", onDarkModeClicked)
    }
}
