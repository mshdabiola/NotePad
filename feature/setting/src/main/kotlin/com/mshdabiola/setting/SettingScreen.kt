/*
 *abiola 2022
 */

package com.mshdabiola.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag // Make sure this is imported
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mshdabiola.designsystem.icon.NoteIcon
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.designsystem.R as Rd

// It's a good practice to define test tags as constants
object SettingScreenTestTags {
    const val SCREEN = "setting:screen"
    const val TOP_APP_BAR = "setting:topAppBar"
    const val BACK_BUTTON = "setting:backButton"
    const val TITLE = "setting:title" // Optional, but can be useful
    const val THEME_ITEM = "setting:theme" // Keeping your existing one
    const val MODE_ITEM = "setting:mode" // Keeping your existing one

    // You can also add tags for the supporting text within ListItems if needed
    const val THEME_SUPPORTING_TEXT = "setting:themeSupportingText"
    const val MODE_SUPPORTING_TEXT = "setting:modeSupportingText"
}

@Preview
@Composable
internal fun SettingScreenPreview() {
    val settingState = SettingState(
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.LIGHT,
    )
    SettingScreen(
        settingState = settingState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(
    modifier: Modifier = Modifier,
    settingState: SettingState,
    onTheme: () -> Unit = {},
    onDarkMode: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.testTag(SettingScreenTestTags.SCREEN), // Tag for the whole screen
        topBar = {
            TopAppBar(
                modifier = Modifier.testTag(SettingScreenTestTags.TOP_APP_BAR), // Tag for the TopAppBar
                title = {
                    Text(
                        text = stringResource(Rd.string.modules_designsystem_settings),
                        modifier = Modifier.testTag(SettingScreenTestTags.TITLE), // Tag for the title
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag(SettingScreenTestTags.BACK_BUTTON), // Tag for back button
                    ) {
                        Icon(imageVector = NoteIcon.ArrowBack, contentDescription = "back")
                    }
                },
            )
        },
    ) {
        Column(
            Modifier // Removed modifier parameter here as it's applied to Scaffold
                .padding(it)
                .padding(16.dp)
                .testTag("setting:contentColumn"), // Optional: tag for the content column
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ListItem(
                modifier = Modifier
                    .testTag(SettingScreenTestTags.THEME_ITEM) // Using the constant
                    .clickable { onTheme() },
                headlineContent = { Text(stringResource(Rd.string.modules_designsystem_theme)) },
                supportingContent = {
                    Text(
                        text = stringArrayResource(Rd.array.modules_designsystem_theme)[settingState.themeBrand.ordinal],
                        modifier = Modifier.testTag(SettingScreenTestTags.THEME_SUPPORTING_TEXT), // Tag for supporting text
                    )
                },
            )

            ListItem(
                modifier = Modifier
                    .testTag(SettingScreenTestTags.MODE_ITEM) // Using the constant
                    .clickable { onDarkMode() },
                headlineContent = { Text(stringResource(Rd.string.modules_designsystem_daynight_mode)) },
                supportingContent = {
                    Text(
                        text = stringArrayResource(Rd.array.modules_designsystem_daynight)[settingState.darkThemeConfig.ordinal],
                        modifier = Modifier.testTag(SettingScreenTestTags.MODE_SUPPORTING_TEXT), // Tag for supporting text
                    )
                },
            )
        }
    }
}
