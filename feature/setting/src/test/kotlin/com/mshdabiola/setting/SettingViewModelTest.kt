/*
 *abiola 2022
 */

package com.mshdabiola.setting

import app.cash.turbine.test
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.model.DarkThemeConfig
import com.mshdabiola.model.ThemeBrand
import com.mshdabiola.testing.fake.testDataModule
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingViewModelTest : KoinTest {

    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 3)
    val koinTestRule = KoinTestRule.create {
        this.modules(testDataModule)
    }
    private val userRepository by inject<UserDataRepository>()

    @Test
    fun init() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = SettingViewModel(
            userDataRepository = userRepository,
        )

        viewModel
            .settingState
            .test {
                var state = awaitItem()

                assertTrue(state is SettingState.Loading)

                state = awaitItem()

                assertTrue(state is SettingState.Success)

                assertEquals(
                    SettingState.Success(
                        themeBrand = ThemeBrand.DEFAULT,
                        darkThemeConfig = DarkThemeConfig.DARK,
                    ),
                    state,
                )

                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun setThemeTest() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = SettingViewModel(
            userDataRepository = userRepository,
        )

        viewModel
            .settingState
            .test {
                var state = awaitItem()

                assertTrue(state is SettingState.Loading)

                state = awaitItem()

                assertTrue(state is SettingState.Success)

                viewModel.setThemeBrand(com.mshdabiola.model.ThemeBrand.GREEN)

                state = awaitItem()

                assertTrue(state is SettingState.Loading)

                state = awaitItem()

                assertTrue(state is SettingState.Success)

                assertEquals(state.themeBrand, com.mshdabiola.model.ThemeBrand.GREEN)

                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun setDarkTest() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = SettingViewModel(
            userDataRepository = userRepository,
        )

        viewModel
            .settingState
            .test {
                var state = awaitItem()

                assertTrue(state is SettingState.Loading)

                state = awaitItem()

                assertTrue(state is SettingState.Success)

                viewModel.setDarkThemeConfig(com.mshdabiola.model.DarkThemeConfig.LIGHT)

                state = awaitItem()

                assertTrue(state is SettingState.Loading)

                state = awaitItem()

                assertTrue(state is SettingState.Success)

                assertEquals(DarkThemeConfig.LIGHT, state.darkThemeConfig)

                cancelAndIgnoreRemainingEvents()
            }
    }
}
