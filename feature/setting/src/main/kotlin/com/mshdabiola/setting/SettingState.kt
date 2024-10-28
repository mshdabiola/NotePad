package com.mshdabiola.setting

import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand

sealed class SettingState {

    data class Loading(val isLoading: Boolean = false) : SettingState()
    data class Success(
        val themeBrand: ThemeBrand = ThemeBrand.DEFAULT,
        val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.DARK,
    ) : SettingState()

    data class Error(val exception: Throwable) : SettingState()
}

fun SettingState.getSuccess(value: (SettingState.Success) -> SettingState.Success): SettingState {
    return if (this is SettingState.Success) {
        value(this)
    } else {
        this
    }
}
