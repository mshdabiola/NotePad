/*
 *abiola 2024
 */

package com.mshdabiola.testing.fake

import com.mshdabiola.common.IAlarmManager
import com.mshdabiola.common.IContentManager
import com.mshdabiola.common.INotePlayer
import com.mshdabiola.common.di.CommonModule
import com.mshdabiola.testing.fake.repository.FakeAlarmManager
import com.mshdabiola.testing.fake.repository.FakeContentManager
import com.mshdabiola.testing.fake.repository.FakeVoicePlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CommonModule::class],
)
internal interface TestCommonModule {

    @Binds
    fun bindsAlarmManager(
        alarmManager: FakeAlarmManager,
    ): IAlarmManager

    @Binds
    fun bindsContentManager(
        contentManager: FakeContentManager,
    ): IContentManager

    @Binds
    fun bindsNotePlayer(
        notePlayer: FakeVoicePlayer,
    ): INotePlayer
}
