/*
 *abiola 2024
 */

package com.mshdabiola.data.di

import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NoteCheckRepository
import com.mshdabiola.data.repository.NoteDrawingRepository
import com.mshdabiola.data.repository.NoteImageRepository
import com.mshdabiola.data.repository.NoteLabelRepository
import com.mshdabiola.data.repository.NoteNotificationRepository
import com.mshdabiola.data.repository.NoteRepository
import com.mshdabiola.data.repository.NoteVoiceRepository
import com.mshdabiola.data.repository.RealLabelRepository
import com.mshdabiola.data.repository.RealNoteCheckRepository
import com.mshdabiola.data.repository.RealNoteDrawingRepository
import com.mshdabiola.data.repository.RealNoteImageRepository
import com.mshdabiola.data.repository.RealNoteLabelRepository
import com.mshdabiola.data.repository.RealNoteRepository
import com.mshdabiola.data.repository.RealNoteVoiceRepository
import com.mshdabiola.data.repository.RealNotificationRepository
import com.mshdabiola.data.repository.RealUserDataRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.data.util.ConnectivityManagerNetworkMonitor
import com.mshdabiola.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: RealUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindNoteRepository(
        noteRepository: RealNoteRepository,
    ): NoteRepository

    @Binds
    internal abstract fun bindNoteCheckRepository(
        noteCheckRepository: RealNoteCheckRepository,
    ): NoteCheckRepository

    @Binds
    internal abstract fun bindNoteDrawingRepository(
        noteDrawingRepository: RealNoteDrawingRepository,
    ): NoteDrawingRepository

    @Binds
    internal abstract fun bindNoteImageRepository(
        noteImageRepository: RealNoteImageRepository,
    ): NoteImageRepository

    @Binds
    internal abstract fun bindNoteLabelRepository(
        noteLabelRepository: RealNoteLabelRepository,
    ): NoteLabelRepository

    @Binds
    internal abstract fun bindNoteNotificationRepository(
        notificationRepository: RealNotificationRepository,
    ): NoteNotificationRepository

    @Binds
    internal abstract fun bindNoteVoiceRepository(
        noteVoiceRepository: RealNoteVoiceRepository,
    ): NoteVoiceRepository

    @Binds
    internal abstract fun bindsLabelRepository(
        realLabelRepository: RealLabelRepository,
    ): LabelRepository
}
