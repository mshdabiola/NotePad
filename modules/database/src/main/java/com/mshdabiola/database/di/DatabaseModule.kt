package com.mshdabiola.database.di

import android.content.Context
import androidx.room.Room
import com.mshdabiola.database.NoteDatabase
import com.mshdabiola.database.dao.LabelDao
import com.mshdabiola.database.dao.NoteCheckDao
import com.mshdabiola.database.dao.NoteDao
import com.mshdabiola.database.dao.NoteDrawingDao
import com.mshdabiola.database.dao.NoteImageDao
import com.mshdabiola.database.dao.NoteLabelDao
import com.mshdabiola.database.dao.NoteNotificationDao
import com.mshdabiola.database.dao.NoteVoiceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun databaseProvider(
        @ApplicationContext context: Context,
    ): NoteDatabase {
        return Room.databaseBuilder(context, NoteDatabase::class.java, "ludoDb.db")
            .build()
//        return Room.inMemoryDatabaseBuilder(context,LudoDatabase::class.java,)
//            .build()
    }

    @Provides
    @Singleton
    fun labelDaoProvider(noteDatabase: NoteDatabase): LabelDao {
        return noteDatabase.getLabelDao()
    }

    @Provides
    @Singleton
    fun noteCheckDaoProvider(noteDatabase: NoteDatabase): NoteCheckDao {
        return noteDatabase.getNoteCheckDao()
    }

    @Provides
    @Singleton
    fun noteDaoProvider(noteDatabase: NoteDatabase): NoteDao {
        return noteDatabase.getNoteDao()
    }

    @Provides
    @Singleton
    fun noteImageDaoProvider(noteDatabase: NoteDatabase): NoteImageDao {
        return noteDatabase.getNoteImageDao()
    }

    @Provides
    @Singleton
    fun noteLabelDaoProvider(noteDatabase: NoteDatabase): NoteLabelDao {
        return noteDatabase.getNoteLabelDao()
    }

    @Provides
    @Singleton
    fun noteVoiceDaoProvider(noteDatabase: NoteDatabase): NoteVoiceDao {
        return noteDatabase.getNoteVoiceDao()
    }

    @Provides
    @Singleton
    fun noteDrawingDaoProvider(noteDatabase: NoteDatabase): NoteDrawingDao {
        return noteDatabase.getNoteDrawingDao()
    }

    @Provides
    @Singleton
    fun notificationDaoProvider(noteDatabase: NoteDatabase): NoteNotificationDao {
        return noteDatabase.getNotification()
    }
}
