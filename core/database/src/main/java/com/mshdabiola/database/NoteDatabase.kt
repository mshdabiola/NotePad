package com.mshdabiola.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mshdabiola.database.dao.GeneralDao
import com.mshdabiola.database.model.NoteCheckEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteVoiceEntity

@Database(
    entities = [
        NoteEntity::class,
        NoteVoiceEntity::class,
        NoteImageEntity::class,
        NoteCheckEntity::class
    ],
    version = 1
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getGeneralDao(): GeneralDao
//
//    abstract fun getPlayerDao(): PlayerDao
//
//    abstract fun getPawnDao(): PawnDao
}
