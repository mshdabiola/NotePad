package com.mshdabiola.testing.fake.repository

import com.mshdabiola.common.INotePlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeVoicePlayer @Inject constructor() : INotePlayer {
    override fun playMusic(path: String, position: Int): Flow<Int> {
        return flow { 2 }
    }

    override fun pause() {
    }
}
