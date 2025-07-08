package com.mshdabiola.setting

import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand

data class SettingState(
    val themeBrand: ThemeBrand = ThemeBrand.DEFAULT,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.DARK,
)
