package com.mshdabiola.setting

import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand

sealed class SettingState {

    data object Loading : SettingState()
    data class Success(
        val themeBrand: ThemeBrand = ThemeBrand.DEFAULT,
        val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.DARK,
    ) : SettingState()
}
