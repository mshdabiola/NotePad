/*
 *abiola 2023
 */

package com.mshdabiola.setting

import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.theme.NotePadTheme
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.ui.PreviewMain

class SettingScreenScreenshotTests {
    @PreviewMain
    @Composable
    internal fun Main() {
        val settingState = SettingState.Success(
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.LIGHT,
        )
        NotePadTheme {
            SettingScreen(
                settingState = settingState,
            )
        }
    }

    @PreviewMain
    @Composable
    internal fun Loading() {
        val settingState = SettingState.Loading
        NotePadTheme {
            SettingScreen(
                settingState = settingState,
            )
        }
    }
}
