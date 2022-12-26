package com.mshdabiola.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.dao.NotepadDao
import com.mshdabiola.database.model.LabelEntity
import com.mshdabiola.database.model.NoteCheckEntity
import com.mshdabiola.database.model.NoteEntity
import com.mshdabiola.database.model.NoteImageEntity
import com.mshdabiola.database.model.NoteLabelEntity
import com.mshdabiola.database.model.NoteVoiceEntity

@Database(
    entities = [
        NoteEntity::class,
        NoteVoiceEntity::class,
        NoteImageEntity::class,
        NoteCheckEntity::class,
        NoteLabelEntity::class,
        LabelEntity::class
    ],
    version = 1
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getLabelDao(): LabelDao

    abstract fun getNoteCheckDao(): NoteCheckDao

    abstract fun getNoteDao(): NoteDao

    abstract fun getNoteImageDao(): NoteImageDao

    abstract fun getNoteLabelDao(): NoteLabelDao

    abstract fun getNoteVoiceDao(): NoteVoiceDao

    abstract fun getNotePadDao(): NotepadDao
}
