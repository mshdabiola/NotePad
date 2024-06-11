/*
 *abiola 2022
 */

package com.mshdabiola.data.repository.fake

import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.datastore.SkPreferencesDataSource
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
    private val skPreferencesDataSource: SkPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        skPreferencesDataSource.userData

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        skPreferencesDataSource.setThemeBrand(themeBrand)
    }

    override suspend fun setThemeContrast(contrast: Contrast) {
        skPreferencesDataSource.setThemeContrast(contrast)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        skPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        skPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        skPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
    }
}
