/*
 *abiola 2022
 */

package com.mshdabiola.main

import app.cash.turbine.test
import com.mshdabiola.common.result.Result
import com.mshdabiola.testing.repository.TestNoteRepository
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.testing.util.TestAnalyticsHelper
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val analyticsHelper = TestAnalyticsHelper()
    private val userDataRepository = TestUserDataRepository()
    private val noteRepository = TestNoteRepository()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(
            userDataRepository = userDataRepository,
            noteRepository = noteRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel
            .feedUiMainState
            .test {
                var state = awaitItem()

                assertTrue(state is Result.Loading)

                state = awaitItem()

                assertTrue(state is Result.Success)

                assertEquals(
                    10,
                    state.data.size,

                )

                cancelAndIgnoreRemainingEvents()
            }
    }
}
