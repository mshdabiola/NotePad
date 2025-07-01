package com.mshdabiola.testing.repository

import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.model.NoteImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TestNoteImageRepository : NoteImageRepository {
    private val images = mutableListOf<NoteImage>()
    private var nextId = 1L

    override suspend fun upserts(images: List<NoteImage>): List<Long> {
        val ids = mutableListOf<Long>()
        images.forEach { image ->
            ids.add(upsert(image))
        }
        return ids
    }

    override suspend fun upsert(image: NoteImage): Long {
        return if (image.id == -1L) {
            val newImage = image.copy(id = nextId++)
            images.add(newImage)
            newImage.id
        } else {
            val index = images.indexOfFirst { it.id == image.id }
            if (index != -1) {
                images[index] = image
                image.id
            } else {
                val newImage = image.copy(id = nextId++)
                images.add(newImage)
                newImage.id
            }
        }
    }

    override suspend fun delete(id: Long) {
        images.removeIf { it.id == id }
    }

    override suspend fun deleteByNoteId(noteId: Long) {
        images.removeIf { it.noteId == noteId }
    }

    override fun getAll(): Flow<List<NoteImage>> {
        return flowOf(images.toList())
    }

    override fun getByNoteId(noteId: Long): Flow<List<NoteImage>> {
        return flowOf(images.filter { it.noteId == noteId }.toList())
    }

    override fun get(id: Long): Flow<NoteImage?> {
        return flowOf(images.find { it.id == id })
    }
}
