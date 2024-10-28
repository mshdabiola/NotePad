/*
 *abiola 2022
 */

package com.mshdabiola.setting

import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test

class SettingViewModelTest {

    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @get:Rule(order = 2)
    val mainDispatcherRule = MainDispatcherRule()
    private val userDataRepository = TestUserDataRepository()

    @Test
    fun init() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = SettingViewModel(
//            userDataRepository = userDataRepository,
//        )
//
//        viewModel
//            .settingState
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is SettingState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Success)
//
//                assertEquals(
//                    SettingState.Success(
//                        themeBrand = ThemeBrand.DEFAULT,
//                        darkThemeConfig = DarkThemeConfig.DARK,
//                    ),
//                    state,
//                )
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }

    @Test
    fun setThemeTest() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = SettingViewModel(
//            userDataRepository = userDataRepository,
//        )
//
//        viewModel
//            .settingState
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is SettingState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Success)
//
//                viewModel.setThemeBrand(ThemeBrand.PINK)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Success)
//
//                assertEquals(state.themeBrand, ThemeBrand.PINK)
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }

    @Test
    fun setDarkTest() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = SettingViewModel(
//            userDataRepository = userDataRepository,
//        )
//
//        viewModel
//            .settingState
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is SettingState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Success)
//
//                viewModel.setDarkThemeConfig(com.mshdabiola.model.DarkThemeConfig.LIGHT)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is SettingState.Success)
//
//                assertEquals(DarkThemeConfig.LIGHT, state.darkThemeConfig)
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }
}
