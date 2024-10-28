/*
 *abiola 2022
 */

package com.mshdabiola.setting.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.dialog
import com.mshdabiola.setting.SettingRoute

fun NavController.navigateToSetting(navOptions: NavOptions = androidx.navigation.navOptions { }) = navigate(
    Setting,
    navOptions,
)

fun NavGraphBuilder.settingScreen(
    modifier: Modifier,
    onShowSnack: suspend (String, String?) -> Boolean,
    onBack: () -> Unit,
) {
    dialog<Setting> {
        SettingRoute(
            modifier = modifier,
            onShowSnack = onShowSnack,
            onBack = onBack,
        )
    }
}
