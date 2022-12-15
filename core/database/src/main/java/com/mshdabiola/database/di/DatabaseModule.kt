package com.mshdabiola.database.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

//    @Provides
//    @Singleton
//    fun databaseProvider(
//        @ApplicationContext context: Context
//    ): LudoDatabase {
//        return Room.databaseBuilder(context, NoteDatabase::class.java, "ludoDb.db")
//            .build()
////        return Room.inMemoryDatabaseBuilder(context,LudoDatabase::class.java,)
////            .build()
//    }

//    @Provides
//    @Singleton
//    fun playerDaoProvider(ludoDatabase: LudoDatabase): PlayerDao {
//        return ludoDatabase.getPlayerDao()
//    }
//
//    @Provides
//    @Singleton
//    fun pawnDaoProvider(ludoDatabase: LudoDatabase): PawnDao {
//        return ludoDatabase.getPawnDao()
//    }
//
//    @Provides
//    @Singleton
//    fun ludoDaoProvider(ludoDatabase: LudoDatabase): LudoDao {
//        return ludoDatabase.getLudoDao()
//    }
}
