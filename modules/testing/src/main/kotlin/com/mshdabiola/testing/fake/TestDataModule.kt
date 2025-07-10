/*
 *abiola 2024
 */

package com.mshdabiola.testing.fake

import com.mshdabiola.data.di.DataModule
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteCheckRepository
import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.data.util.NetworkMonitor
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteCheckRepository
import com.mshdabiola.testing.fake.repository.FakeNoteDrawingRepository
import com.mshdabiola.testing.fake.repository.FakeNoteImageRepository
import com.mshdabiola.testing.fake.repository.FakeNoteLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNoteRepository
import com.mshdabiola.testing.fake.repository.FakeNoteVoiceRepository
import com.mshdabiola.testing.fake.repository.FakeNotificationRepository
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
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun bindsUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository

    @Binds
    fun bindNoteRepository(
        noteRepository: FakeNoteRepository,
    ): NoteRepository

    @Binds
    fun bindNoteCheckRepository(
        noteCheckRepository: FakeNoteCheckRepository,
    ): NoteCheckRepository

    @Binds
    fun bindNoteDrawingRepository(
        noteDrawingRepository: FakeNoteDrawingRepository,
    ): NoteDrawingRepository

    @Binds
    fun bindNoteImageRepository(
        noteImageRepository: FakeNoteImageRepository,
    ): NoteImageRepository

    @Binds
    fun bindNoteLabelRepository(
        noteLabelRepository: FakeNoteLabelRepository,
    ): NoteLabelRepository

    @Binds
    fun bindNoteNotificationRepository(
        notificationRepository: FakeNotificationRepository,
    ): NoteNotificationRepository

    @Binds
    fun bindNoteVoiceRepository(
        noteVoiceRepository: FakeNoteVoiceRepository,
    ): NoteVoiceRepository

    @Binds
    fun bindsLabelRepository(
        realLabelRepository: FakeLabelRepository,
    ): LabelRepository
}
