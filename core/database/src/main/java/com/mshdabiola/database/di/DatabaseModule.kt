package com.mshdabiola.database.di

import android.content.Context
import androidx.room.Room
import com.mshdabiola.database.NoteDatabase
import com.mshdabiola.database.dao.GeneralDao
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
        @ApplicationContext context: Context
    ): NoteDatabase {
        return Room.databaseBuilder(context, NoteDatabase::class.java, "ludoDb.db")
            .build()
//        return Room.inMemoryDatabaseBuilder(context,LudoDatabase::class.java,)
//            .build()
    }

    @Provides
    @Singleton
    fun generalDaoProvider(noteDatabase: NoteDatabase): GeneralDao {
        return noteDatabase.getGeneralDao()
    }

}
