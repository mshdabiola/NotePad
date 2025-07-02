/*
 *abiola 2023
 */

package com.mshdabiola.setting

import androidx.compose.runtime.Composable
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.testing.util.PreviewAllLocales
import com.mshdabiola.ui.PreviewContainer

@PreviewAllLocales
@Composable
internal fun SettingScreenShot() {
    val settingState = SettingState.Success(
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.LIGHT,
    )
    PreviewContainer {
        SettingScreen(
            settingState = settingState,
        )
    }
}

@PreviewAllLocales
@Composable
internal fun SettingLoadingScreenShot() {
    val settingState = SettingState.Loading
    PreviewContainer {
        SettingScreen(
            settingState = settingState,
        )
    }
}
