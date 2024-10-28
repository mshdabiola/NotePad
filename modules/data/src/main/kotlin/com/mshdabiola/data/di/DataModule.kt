/*
 *abiola 2024
 */

package com.mshdabiola.data.di

import com.mshdabiola.data.repository.DrawingPathRepository
import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.LabelRepository
import com.mshdabiola.data.repository.NotePadRepository
import com.mshdabiola.data.repository.OfflineFirstUserDataRepository
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
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsDrawingPathRepository(
        drawingPathRepository: DrawingPathRepository,
    ): IDrawingPathRepository

    @Binds
    internal abstract fun bindsLabelRepository(
        labelRepository: LabelRepository,
    ): ILabelRepository

    @Binds
    internal abstract fun bindsNotePadRepository(
        notePadRepository: NotePadRepository,
    ): INotePadRepository
}
