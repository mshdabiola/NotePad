/*
 *abiola 2024
 */

package com.mshdabiola.data.repository

import com.mshdabiola.analytics.AnalyticsHelper
import com.mshdabiola.common.network.Dispatcher
import com.mshdabiola.common.network.NoteDispatchers
import com.mshdabiola.datastore.UserPreferencesRepository
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RealUserDataRepository @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsHelper: AnalyticsHelper,
    @Dispatcher(NoteDispatchers.IO)
    private val dispatcher: CoroutineDispatcher,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        userPreferencesRepository.userData

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        withContext(dispatcher) {
            userPreferencesRepository.setThemeBrand(themeBrand)
            analyticsHelper.logThemeChanged(themeBrand.name)
        }
    }

    override suspend fun setThemeContrast(contrast: Contrast) {
        withContext(dispatcher) {
            userPreferencesRepository.setThemeContrast(contrast)
            analyticsHelper.logContrastChanged(contrast.name)
        }
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        withContext(dispatcher) {
            userPreferencesRepository.setDarkThemeConfig(darkThemeConfig)
            analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
        } }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        withContext(dispatcher) {
            userPreferencesRepository.setDynamicColorPreference(useDynamicColor)
            analyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor)
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        withContext(dispatcher) {
            userPreferencesRepository.setShouldHideOnboarding(shouldHideOnboarding)
            analyticsHelper.logOnboardingStateChanged(shouldHideOnboarding)
        }
    }

    override suspend fun setNoteDisplayCategory(noteDisplayCategory: NoteDisplayCategory) {
        withContext(dispatcher) { userPreferencesRepository.setNoteDisplayCategory(noteDisplayCategory) }
    }

    override suspend fun toggleGrid() {
        withContext(dispatcher) { userPreferencesRepository.toggleGrid() }
    }
}
