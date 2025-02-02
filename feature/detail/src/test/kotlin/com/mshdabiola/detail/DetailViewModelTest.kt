/*
 *abiola 2022
 */

package com.mshdabiola.detail

import androidx.lifecycle.SavedStateHandle
import com.mshdabiola.testing.repository.TestAlarmManager
import com.mshdabiola.testing.repository.TestNotePadRepository
import com.mshdabiola.testing.repository.TestUserDataRepository
import com.mshdabiola.testing.repository.TestVoicePlayer
import com.mshdabiola.testing.util.MainDispatcherRule
import com.mshdabiola.testing.util.TestAnalyticsHelper
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class DetailViewModelTest {
    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private val analyticsHelper = TestAnalyticsHelper()
    private val userDataRepository = TestUserDataRepository()
    private val noteRepository = TestNotePadRepository()
    private val alarmManager = TestAlarmManager()
    private val voicePlayer = TestVoicePlayer()

    private val savedStateHandle = SavedStateHandle(mapOf("id" to 1))

    @Test
    fun init() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = DetailViewModel(
//            savedStateHandle = savedStateHandle,
//            notePadRepository = noteRepository,
//            alarmManager = alarmManager,
//            voicePlayer = voicePlayer,
//        )
//
//        viewModel
//            .state
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is DetailState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is DetailState.Success)
//
//                assertEquals(
//                    1,
//                    state.id,
//
//                )
//
//                val note = noteRepository.getOne(1).first()
//                assertEquals(
//                    note?.title,
//                    viewModel.title.text.toString(),
//                )
//                assertEquals(
//                    note?.content,
//                    viewModel.content.text.toString(),
//                )
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }

    @Test
    fun update() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = DetailViewModel(
//            savedStateHandle = savedStateHandle,
//            noteRepository = noteRepository,
//        )
//
//        viewModel
//            .state
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is DetailState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is DetailState.Success)
//
//                assertEquals(
//                    1,
//                    state.id,
//
//                )
//
//                viewModel.title.clearText()
//                viewModel.title.edit {
//                    append("new title")
//                }
//                viewModel.content.clearText()
//                viewModel.content.edit {
//                    append("new content")
//                }
//                delay(1000)
//
//                val note = noteRepository.getOne(1).first()
//                assertEquals(
//                    "new title",
//                    note?.title,
//                )
//                assertEquals(
//                    "new content",
//                    note?.content,
//                )
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }

    @Test
    fun init_new() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = DetailViewModel(
//            savedStateHandle = savedStateHandle,
//            noteRepository = noteRepository,
//        )
//
//        viewModel
//            .state
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is DetailState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is DetailState.Success)
//
//                assertEquals(
//                    -1,
//                    state.id,
//
//                )
//
//                assertEquals(
//                    "",
//                    viewModel.title.text.toString(),
//                )
//                assertEquals(
//                    "",
//                    viewModel.content.text.toString(),
//                )
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }

    @Test
    fun addNew() = runTest(mainDispatcherRule.testDispatcher) {
//        val viewModel = DetailViewModel(
//            savedStateHandle = savedStateHandle,
//            noteRepository = noteRepository,
//        )
//
//        viewModel
//            .state
//            .test {
//                var state = awaitItem()
//
//                assertTrue(state is DetailState.Loading)
//
//                state = awaitItem()
//
//                assertTrue(state is DetailState.Success)
//
//                assertEquals(
//                    -1,
//                    state.id,
//
//                )
//
//                viewModel.title.clearText()
//                viewModel.title.edit {
//                    append("new title")
//                }
//                viewModel.content.clearText()
//                viewModel.content.edit {
//                    append("new content")
//                }
//                delay(1000)
//
//                val note = noteRepository.getAll().first().last()
//                assertEquals(
//                    "new title",
//                    note.title,
//                )
//                assertEquals(
//                    "new content",
//                    note.content,
//                )
//
//                cancelAndIgnoreRemainingEvents()
//            }
    }
}
