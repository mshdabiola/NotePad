/*
 *abiola 2022
 */

package com.mshdabiola.setting.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.setting.SettingScreen
import com.mshdabiola.setting.SettingViewModel

fun NavBackStack.navigateToSetting() {
    add(Setting)
}

fun EntryProviderBuilder<NavKey>.settingScreen(
    modifier: Modifier,
    onBack: () -> Unit,
) {
    entry<Setting> {
        val viewModel = hiltViewModel<SettingViewModel>()
        val settingState = viewModel.settingState.collectAsStateWithLifecycle()

        SettingScreen(
            modifier = modifier,
            settingState = settingState.value,
            setTheme = viewModel::setThemeBrand,
            setDarkMode = viewModel::setDarkThemeConfig,
            onBack = onBack,
        )
    }
}
