package com.mshdabiola.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteVoiceDao
import com.mshdabiola.database.dao.NotepadDao
import com.mshdabiola.database.dao.PathDao
import com.mshdabiola.database.model.DrawPathEntity
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
        LabelEntity::class,
        DrawPathEntity::class,
    ],
    version = 4,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3, NoteDatabase.Migrate2to3::class),
        AutoMigration(3, 4, NoteDatabase.Migrate3to4::class),
    ],
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getLabelDao(): LabelDao

    abstract fun getNoteCheckDao(): NoteCheckDao

    abstract fun getNoteDao(): NoteDao

    abstract fun getNoteImageDao(): NoteImageDao

    abstract fun getNoteLabelDao(): NoteLabelDao

    abstract fun getNoteVoiceDao(): NoteVoiceDao

    abstract fun getNotePadDao(): NotepadDao

    abstract fun getPath(): PathDao

    @DeleteColumn(tableName = "note_image_table", columnName = "imageName")
    class Migrate2to3 : AutoMigrationSpec

    @DeleteColumn(tableName = "note_image_table", columnName = "imageName")
    class Migrate3to4 : AutoMigrationSpec
}
