package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.model.Note
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteImageDaoTest {
    lateinit var db: NoteDatabase
    val noteEntity = Note()

    @Before
    fun before() {
        val content = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(content, NoteDatabase::class.java).build()
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun upsertTest() = runBlocking {
    }

    @Test
    fun deleteTest() = runBlocking {
    }

    @Test
    fun deleteByIdTest() = runBlocking {
    }

    @Test
    fun getOneTest() = runBlocking {
    }

    @Test
    fun getAllTest() = runBlocking {
    }
}
