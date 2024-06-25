/*
 *abiola 2024
 */

package com.mshdabiola.testing.fake

import com.mshdabiola.data.di.DataModule
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.data.util.NetworkMonitor
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
internal interface TestDataModule {

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindsNoteRepository(noteRepository: FakeNoteRepository): NoteRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor
}
