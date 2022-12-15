package com.mshdabiola.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mshdabiola.database.dao.LudoDao
import com.mshdabiola.database.model.LudoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LudoDaoTest {
    lateinit var ludoDao: LudoDao
    lateinit var db: LudoDatabase
    val ludoEntity = LudoEntity()

    @Before
    fun before() {
        val content = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(content, LudoDatabase::class.java).build()
        ludoDao = db.getLudoDao()
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun upsertTest() = runBlocking {
        ludoDao.upsert(ludoEntity)
        ludoDao.upsert(ludoEntity)

        val size = ludoDao.getAll().first().size

        Assert.assertEquals(2, size)
    }

    @Test
    fun deleteTest() = runBlocking {
        val ludoEntity = LudoEntity(1, 3)
        ludoDao.upsert(ludoEntity)
        ludoDao.delete(ludoEntity)

        val size = ludoDao.getAll().first().size

        Assert.assertEquals(0, size)
    }

    @Test
    fun deleteByIdTest() = runBlocking {
        val ludoEntity = LudoEntity(1, 3)
        ludoDao.upsert(ludoEntity)
        ludoDao.deleteById(1)

        val size = ludoDao.getAll().first().size

        Assert.assertEquals(0, size)
    }

    @Test
    fun getOneTest() = runBlocking {

        ludoDao.upsert(ludoEntity)
        val ludo = ludoDao.getOne(1).first()
        println("ludo $ludo")
        Assert.assertEquals(ludoEntity.copy(id = 1), ludo)
    }

    @Test
    fun getAllTest() = runBlocking {
        ludoDao.upsert(ludoEntity)
        ludoDao.upsert(ludoEntity)
        ludoDao.upsert(ludoEntity)
        ludoDao.upsert(ludoEntity)

        val size = ludoDao.getAll().first().size

        Assert.assertEquals(4, size)
    }
}
