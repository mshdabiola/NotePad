/*
 *abiola 2022
 */

package com.mshdabiola.main

import com.mshdabiola.testing.repository.TestAlarmManager
import com.mshdabiola.testing.repository.TestNotePadRepository
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.repository.TestVoicePlayer
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.testing.util.TestAnalyticsHelper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val analyticsHelper = TestAnalyticsHelper()
    private val userDataRepository = TestUserDataRepository()
    private val noteRepository = TestNotePadRepository()
    private val alarmManager = TestAlarmManager()
    private val voicePlayer = TestVoicePlayer()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
//        viewModel = MainViewModel(
//
//        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest(mainDispatcherRule.testDispatcher) {
//        viewModel
//            .feedUiMainState
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is Result.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is Result.Success)
//
//                assertEquals(
//                    10,
//                    state.data.size,
//
//                )
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }
}
