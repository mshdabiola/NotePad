/*
 *abiola 2024
 */

package com.mshdabiola.data.repository

import com.mshdabiola.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun upsert(note: Note): Long
    fun getAll(): Flow<List<Note>>

    fun getOne(id: Long): Flow<Note?>

    suspend fun delete(id: Long)
}
