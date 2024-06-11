/*
 *abiola 2024
 */

package com.mshdabiola.datastore

import androidx.datastore.core.DataStore
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.UserData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SkPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                themeBrand = when (it.themeBrand) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT,
                    -> com.mshdabiola.model.ThemeBrand.DEFAULT

                    ThemeBrandProto.THEME_BRAND_GREEN -> com.mshdabiola.model.ThemeBrand.GREEN
                },
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    ->
                        com.mshdabiola.model.DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        com.mshdabiola.model.DarkThemeConfig.LIGHT

                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> com.mshdabiola.model.DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
                contrast = when (it.contrast) {
                    null,
                    ThemeContrastProto.THEME_CONTRAST_UNSPECIFIED,
                    ThemeContrastProto.UNRECOGNIZED,
                    ThemeContrastProto.THEME_CONTRAST_NORMAL,
                    ->
                        Contrast.Normal

                    ThemeContrastProto.THEME_CONTRAST_HIGH ->
                        Contrast.High

                    ThemeContrastProto.THEME_CONTRAST_MEDIUM -> Contrast.Medium
                },
            )
        }

    suspend fun setThemeBrand(themeBrand: com.mshdabiola.model.ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    com.mshdabiola.model.ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    com.mshdabiola.model.ThemeBrand.GREEN -> ThemeBrandProto.THEME_BRAND_GREEN
                }
            }
        }
    }

    suspend fun setThemeContrast(contrast: Contrast) {
        userPreferences.updateData {
            it.copy {
                this.contrast = when (contrast) {
                    Contrast.Normal -> ThemeContrastProto.THEME_CONTRAST_NORMAL
                    Contrast.High -> ThemeContrastProto.THEME_CONTRAST_HIGH
                    Contrast.Medium -> ThemeContrastProto.THEME_CONTRAST_MEDIUM
                }
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: com.mshdabiola.model.DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    com.mshdabiola.model.DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM

                    com.mshdabiola.model.DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    com.mshdabiola.model.DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}
