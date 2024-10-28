/*
 *abiola 2024
 */

package com.mshdabiola.data.repository

import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.datastore.UserPreferencesRepository
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        userPreferencesRepository.userData

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferencesRepository.setThemeBrand(themeBrand)
        analyticsHelper.logThemeChanged(themeBrand.name)
    }

    override suspend fun setThemeContrast(contrast: Contrast) {
        userPreferencesRepository.setThemeContrast(contrast)
        analyticsHelper.logContrastChanged(contrast.name)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferencesRepository.setDarkThemeConfig(darkThemeConfig)
        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferencesRepository.setDynamicColorPreference(useDynamicColor)
        analyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferencesRepository.setShouldHideOnboarding(shouldHideOnboarding)
        analyticsHelper.logOnboardingStateChanged(shouldHideOnboarding)
    }
}
