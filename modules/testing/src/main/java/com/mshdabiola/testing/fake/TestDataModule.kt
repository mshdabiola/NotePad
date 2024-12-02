/*
 *abiola 2024
 */

package com.mshdabiola.testing.fake

import com.mshdabiola.data.di.DataModule
import com.mshdabiola.data.repository.IDrawingPathRepository
import com.mshdabiola.data.repository.ILabelRepository
import com.mshdabiola.data.repository.INotePadRepository
import com.mshdabiola.data.repository.UserDataRepository
import com.mshdabiola.data.util.NetworkMonitor
import com.mshdabiola.testing.fake.repository.FakeDrawingPathRepository
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeNotePadRepository
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
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor

    @Binds
    fun bindsDrawingPathRepository(
        drawingPathRepository: FakeDrawingPathRepository,
    ): IDrawingPathRepository

    @Binds
    fun bindsLabelRepository(
        labelRepository: FakeLabelRepository,
    ): ILabelRepository

    @Binds
    fun bindsNotePadRepository(
        notePadRepository: FakeNotePadRepository,
    ): INotePadRepository
}
