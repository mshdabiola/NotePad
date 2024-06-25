package com.mshdabiola.data.repository

import com.mshdabiola.model.Note

interface INoteRepository {
    suspend fun upsert(notes: List<Note>)
}
