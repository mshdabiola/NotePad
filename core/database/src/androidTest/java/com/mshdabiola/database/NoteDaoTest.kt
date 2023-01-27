package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.GeneralDao
import com.mshdabiola.database.model.toNoteEntity
import com.mshdabiola.model.Note
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    lateinit var generalDao: GeneralDao
    lateinit var db: NoteDatabase
    val noteEntity = Note()

    @Before
    fun before() {
        val content = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(content, NoteDatabase::class.java).build()
        generalDao = db.getGeneralDao()
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun upsertTest() = runBlocking {
        val id = generalDao.addNote(noteEntity.toNoteEntity())
        val id2 = generalDao.addNote(noteEntity.toNoteEntity())
//        ludoDao.upsert(ludoEntity)
//
//        val size = ludoDao.getAll().first().size
//
        Assert.assertEquals(1, id)
        Assert.assertEquals(3, id2)
    }

    @Test
    fun deleteTest() = runBlocking {
//        val ludoEntity = LudoEntity(1, 3)
//        ludoDao.upsert(ludoEntity)
//        ludoDao.delete(ludoEntity)
//
//        val size = ludoDao.getAll().first().size
//
//        Assert.assertEquals(0, size)
    }

    @Test
    fun deleteByIdTest() = runBlocking {
//        val ludoEntity = LudoEntity(1, 3)
//        ludoDao.upsert(ludoEntity)
//        ludoDao.deleteById(1)
//
//        val size = ludoDao.getAll().first().size
//
//        Assert.assertEquals(0, size)
    }

    @Test
    fun getOneTest() = runBlocking {
//        ludoDao.upsert(ludoEntity)
//        val ludo = ludoDao.getOne(1).first()
//        println("ludo $ludo")
//        Assert.assertEquals(ludoEntity.copy(id = 1), ludo)
    }

    @Test
    fun getAllTest() = runBlocking {
//        ludoDao.upsert(ludoEntity)
//        ludoDao.upsert(ludoEntity)
//        ludoDao.upsert(ludoEntity)
//        ludoDao.upsert(ludoEntity)
//
//        val size = ludoDao.getAll().first().size
//
//        Assert.assertEquals(4, size)
    }
}
