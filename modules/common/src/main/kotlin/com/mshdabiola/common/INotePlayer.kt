package com.mshdabiola.common

import kotlinx.coroutines.flow.Flow

interface INotePlayer {
    fun playMusic(path: String, position: Int): Flow<Int>
    fun pause()
}
