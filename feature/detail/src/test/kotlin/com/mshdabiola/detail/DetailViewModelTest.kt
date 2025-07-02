/*
 *abiola 2022
 */

package com.mshdabiola.detail

import app.cash.turbine.test
import com.mshdabiola.detail.navigation.DetailArg
import com.mshdabiola.domain.AddAllNoteUseCase
import com.mshdabiola.domain.AudioLengthUseCase
import com.mshdabiola.domain.DateUseCase
import com.mshdabiola.domain.GetNoteUseCase
import com.mshdabiola.domain.LinkUriUseCase
import com.mshdabiola.model.Note
import com.mshdabiola.model.NotePad
import com.mshdabiola.testing.repository.TestAlarmManager
import com.mshdabiola.testing.repository.TestContentManager
import com.mshdabiola.testing.repository.TestNoteCheckRepository
import com.mshdabiola.testing.repository.TestNoteDrawingRepository
import com.mshdabiola.testing.repository.TestNoteImageRepository
import com.mshdabiola.testing.repository.TestNoteLabelRepository
import com.mshdabiola.testing.repository.TestNoteRepository
import com.mshdabiola.testing.repository.TestNoteVoiceRepository
import com.mshdabiola.testing.repository.TestNotificationRepository
import com.mshdabiola.testing.repository.TestVoicePlayer
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DetailViewModelTest {
    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    private val alarmManager = TestAlarmManager()
    private val voicePlayer = TestVoicePlayer()
    private val noteRepository = TestNoteRepository()
    private val contentManager = TestContentManager()
    private val audioLengthUseCase = AudioLengthUseCase()
    private val linkUriUseCase = LinkUriUseCase()
    val getNoteUseCase = GetNoteUseCase(
        noteRepository = noteRepository,
        audioLengthUseCase = audioLengthUseCase,
        linkUriUseCase = linkUriUseCase,
        contentManager = contentManager,
    )
    val noteCheckRepository = TestNoteCheckRepository()
    val voiceRepository = TestNoteVoiceRepository()

    val allNoteUseCase = AddAllNoteUseCase(
        noteRepository = noteRepository,
        noteCheckRepository = noteCheckRepository,
        noteDrawingRepository = TestNoteDrawingRepository(),
        noteImageRepository = TestNoteImageRepository(),
        noteLabelRepository = TestNoteLabelRepository(),
        noteNotificationRepository = TestNotificationRepository(),
        noteVoiceRepository = voiceRepository,
    )

    val detailArg = DetailArg(
        id = 1,
        colorIndex = 0,
        background = 0,
    )
    private val initState = DetailState(
        notePad = NotePad(
            note = Note(
                id = detailArg.id,
                color = detailArg.colorIndex,
                background = detailArg.background,
            ),
        ),
    )

    @Test
    fun init() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = DetailViewModel(
            detailArg = detailArg,
            voicePlayer = voicePlayer,
            getNoteUseCase = getNoteUseCase,
            contentManager = contentManager,
            addAllNoteUseCase = allNoteUseCase,
            dateUseCase = DateUseCase(),
            noteCheckRepository = noteCheckRepository,
            noteVoiceRepository = voiceRepository,
        )

        viewModel
            .detailState
            .test {
                var state = awaitItem()

                assertEquals(initState.notePad, state.notePad)

//                state = awaitItem()
//
//                assertEquals(initState.notePad, state.notePad)

                cancelAndIgnoreRemainingEvents()
            }
    }
}
