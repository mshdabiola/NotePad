/*
 *abiola 2022
 */

package com.mshdabiola.testing.fake.repository

import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import com.mshdabiola.testing.repository.emptyUserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserDataRepository @Inject constructor() : UserDataRepository {
    /**
     * The backing hot flow for the list of followed topic ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()

    init {
        setUserData(emptyUserData)
    }
    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(themeBrand = themeBrand))
        }
    }

    override suspend fun setThemeContrast(contrast: Contrast) {
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
        }
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(useDynamicColor = useDynamicColor))
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(shouldHideOnboarding = shouldHideOnboarding))
        }
    }

    override suspend fun setNoteDisplayCategory(noteDisplayCategory: NoteDisplayCategory) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(noteDisplayCategory = noteDisplayCategory))
        }
    }

    override suspend fun toggleGrid() {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(isGrid = !current.isGrid))
        }
    }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(userData)
    }
}
