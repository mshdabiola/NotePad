/*
 *abiola 2022
 */

package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.datastore.UserPreferencesRepository
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserDataRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        userPreferencesRepository.userData

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferencesRepository.setThemeBrand(themeBrand)
    }

    override suspend fun setThemeContrast(contrast: Contrast) {
        userPreferencesRepository.setThemeContrast(contrast)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferencesRepository.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferencesRepository.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferencesRepository.setShouldHideOnboarding(shouldHideOnboarding)
    }
}
