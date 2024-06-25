/*
 *abiola 2024
 */

package com.mshdabiola.common.di

import com.mshdabiola.common.AlarmManager
import com.mshdabiola.common.ContentManager
import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.common.IContentManager
import com.mshdabiola.common.INotePlayer
import com.mshdabiola.common.NotePlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    internal abstract fun bindsAlarmManager(
        alarmManager: AlarmManager,
    ): IAlarmManager

    @Binds
    internal abstract fun bindsContentManager(
        contentManager: ContentManager,
    ): IContentManager

    @Binds
    internal abstract fun bindsNotePlayer(
        notePlayer: NotePlayer,
    ): INotePlayer
}
