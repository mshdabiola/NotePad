package com.mshdabiola.testing.repository

import com.mshdabiola.common.INotePlayer
import kotlinx.coroutines.flow.Flow

class TestVoicePlayer : INotePlayer {
    override fun playMusic(path: String, position: Int): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }
}
