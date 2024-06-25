package com.mshdabiola.data.repository

interface INoteVoiceRepository {
    suspend fun delete(id: Long)
}
