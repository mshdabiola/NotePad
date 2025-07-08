/*
 *abiola 2022
 */

package com.mshdabiola.setting.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.setting.OptionsDialog
import com.mshdabiola.setting.SettingScreen
import com.mshdabiola.setting.SettingViewModel
import com.mshdabiola.designsystem.R as Rd

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
        var dark by remember { mutableStateOf(false) }
        var theme by remember { mutableStateOf(false) }

        SettingScreen(
            modifier = modifier,
            settingState = settingState.value,
            onTheme = { theme = true },
            onDarkMode = { dark = true },
            onBack = onBack,
        )

        if (theme) {
            OptionsDialog(
                modifier = Modifier,
                options = stringArrayResource(Rd.array.modules_designsystem_theme).toList(),
                current = settingState.value.themeBrand.ordinal,
                onDismiss = { theme = false },
                onSelect = { viewModel.setThemeBrand(ThemeBrand.entries[it]) },
            )
        }
        if (dark) {
            OptionsDialog(
                modifier = Modifier,
                options = stringArrayResource(Rd.array.modules_designsystem_daynight).toList(),
                current = settingState.value.darkThemeConfig.ordinal,
                onDismiss = { dark = false },
                onSelect = { viewModel.setDarkThemeConfig(DarkThemeConfig.entries[it]) },
            )
        }
    }
}
