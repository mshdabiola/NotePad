/*
 *abiola 2023
 */

package com.mshdabiola.setting

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mshdabiola.designsystem.theme.darkDefaultScheme
import com.mshdabiola.designsystem.theme.lightDefaultScheme
import com.mshdabiola.ui.DevicePreviews

class ScreenScreenshotTests {

    @DevicePreviews
    @Composable
    fun LoadingLight() {
        MaterialTheme(colorScheme = lightDefaultScheme) {
            Text("Hello")
//            SettingScreen(
//                modifier = Modifier.fillMaxSize(),
//
//                settingState = SettingState.Loading(),
//            )
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @DevicePreviews
    @Composable
    fun LoadingDark() {
        MaterialTheme(colorScheme = darkDefaultScheme) {
            Surface {
                Text("Hello")

//                SettingScreen(
//                    modifier = Modifier.fillMaxSize(),
//                    settingState = SettingState.Loading(),
//                )
            }
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @DevicePreviews
    @Composable
    fun MainLight() {
        MaterialTheme(colorScheme = lightDefaultScheme) {
            Surface {
                Text("Hello")

//                SettingScreen(
//                    modifier = Modifier.fillMaxSize(),
//
//                    settingState = SettingState.Success(
//                        themeBrand = ThemeBrand.DEFAULT,
//                        darkThemeConfig = DarkThemeConfig.DARK,
//                    ),
//                )
            }
        }
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    @DevicePreviews
    @Composable
    fun MainDark() {
        MaterialTheme(colorScheme = darkDefaultScheme) {
            Surface {
                Text("Hello")

//                SettingScreen(
//                    modifier = Modifier.fillMaxSize(),
//                    settingState = SettingState.Success(
//                        themeBrand = ThemeBrand.DEFAULT,
//                        darkThemeConfig = DarkThemeConfig.DARK,
//                    ),
//                )
            }
        }
    }
}
