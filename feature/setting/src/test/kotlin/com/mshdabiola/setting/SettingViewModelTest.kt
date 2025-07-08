/*
 *abiola 2022
 */

package com.mshdabiola.setting

import app.cash.turbine.test
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.model.UserData
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userDataRepository: TestUserDataRepository
    private lateinit var viewModel: SettingViewModel

    // Default UserData for initial setup
    private val defaultUserData = UserData(
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.DARK,
        // Add other UserData properties with default values if they affect SettingState
        isGrid = false, // example
        noteDisplayCategory = com.mshdabiola.model.NoteDisplayCategory(), // example
        useDynamicColor = false, // example
        shouldHideOnboarding = false, // example
        contrast = com.mshdabiola.model.Contrast.Medium, // example
    )

    @Before
    fun setUp() {
        userDataRepository = TestUserDataRepository()
        // Set initial data for the repository
        userDataRepository.setUserData(defaultUserData)
        viewModel = SettingViewModel(userDataRepository = userDataRepository)
    }

    @Test
    fun `settingState emits initial values from UserDataRepository`() = runTest {
        viewModel.settingState.test {
            val initialState = awaitItem()
            assertEquals(ThemeBrand.DEFAULT, initialState.themeBrand)
            assertEquals(DarkThemeConfig.DARK, initialState.darkThemeConfig)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setThemeBrand updates UserDataRepository and settingState`() = runTest {
        val newThemeBrand = ThemeBrand.PINK // Or any other brand for testing

        viewModel.setThemeBrand(newThemeBrand)

        // Verify that the repository method was called correctly
        val updatedUserData = userDataRepository.userData.first()
        assertEquals(newThemeBrand, updatedUserData.themeBrand)

        // Verify that the settingState reflects the change
        viewModel.settingState.test {
            // Potentially skip initial emission if it's quick or already asserted
            // awaitItem() // if needed to skip initial default value

            val updatedState = awaitItem() // This should be the state after the update
            assertEquals(newThemeBrand, updatedState.themeBrand)
            // Dark theme config should remain unchanged from the initial defaultUserData
            assertEquals(defaultUserData.darkThemeConfig, updatedState.darkThemeConfig)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setDarkThemeConfig updates UserDataRepository and settingState`() = runTest {
        val newDarkThemeConfig = DarkThemeConfig.LIGHT

        viewModel.setDarkThemeConfig(newDarkThemeConfig)

        // Verify that the repository method was called correctly
        val updatedUserData = userDataRepository.userData.first()
        assertEquals(newDarkThemeConfig, updatedUserData.darkThemeConfig)

        // Verify that the settingState reflects the change
        viewModel.settingState.test {
            // Potentially skip initial emission
            // awaitItem()

            val updatedState = awaitItem()
            assertEquals(newDarkThemeConfig, updatedState.darkThemeConfig)
            // Theme brand should remain unchanged from the initial defaultUserData
            assertEquals(defaultUserData.themeBrand, updatedState.themeBrand)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `settingState reflects subsequent changes from UserDataRepository`() = runTest {
        viewModel.settingState.test {
            // Initial state
            var currentState = awaitItem()
            assertEquals(ThemeBrand.DEFAULT, currentState.themeBrand)
            assertEquals(DarkThemeConfig.DARK, currentState.darkThemeConfig)

            // Simulate external change in repository for ThemeBrand
            val newThemeBrand = ThemeBrand.PINK
            userDataRepository.setThemeBrand(newThemeBrand)
            currentState = awaitItem() // ViewModel should pick up this change
            assertEquals(newThemeBrand, currentState.themeBrand)
            assertEquals(DarkThemeConfig.DARK, currentState.darkThemeConfig) // Dark theme unchanged

            // Simulate external change in repository for DarkThemeConfig
            val newDarkThemeConfig = DarkThemeConfig.LIGHT
            userDataRepository.setDarkThemeConfig(newDarkThemeConfig)
            currentState = awaitItem() // ViewModel should pick up this change
            assertEquals(newThemeBrand, currentState.themeBrand) // Theme brand unchanged from previous step
            assertEquals(newDarkThemeConfig, currentState.darkThemeConfig)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
