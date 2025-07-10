/*
 *abiola 2024
 */

package com.mshdabiola.datastore

import androidx.datastore.core.DataStore
import com.mshdabiola.model.Contrast
import com.mshdabiola.model.NoteDisplayCategory
import com.mshdabiola.model.UserData
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val userPreferences: DataStore<UserData>,
) {
    val userData = userPreferences.data

    suspend fun setThemeBrand(themeBrand: com.mshdabiola.model.ThemeBrand) {
        userPreferences.updateData {
            it.copy(themeBrand = themeBrand)
        }
    }

    suspend fun setThemeContrast(contrast: Contrast) {
        userPreferences.updateData {
            it.copy(contrast = contrast)
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy(useDynamicColor = useDynamicColor)
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: com.mshdabiola.model.DarkThemeConfig) {
        userPreferences.updateData {
            it.copy(darkThemeConfig = darkThemeConfig)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy(shouldHideOnboarding = shouldHideOnboarding)
        }
    }
    suspend fun toggleGrid() {
        userPreferences.updateData {
            it.copy(isGrid = !it.isGrid)
        }
    }

    suspend fun setNoteDisplayCategory(noteDisplayCategory: NoteDisplayCategory) {
        userPreferences.updateData {
            it.copy(noteDisplayCategory = noteDisplayCategory)
        }
    }
}
