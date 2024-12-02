/*
 *abiola 2024
 */

package com.mshdabiola.testing.datastore

import androidx.datastore.core.DataStoreFactory
import com.mshdabiola.datastore.UserPreferencesSerializer
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder

// @Module
// @TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [DataStoreModule::class],
// )
internal object TestDataStoreModule {
//
//    @Provides
//    @Singleton
//    fun providesUserPreferencesDataStore(
//        @ApplicationScope scope: CoroutineScope,
//        userPreferencesSerializer: UserPreferencesSerializer,
//        tmpFolder: TemporaryFolder,
//    ): DataStore<UserPreferences> =
//        tmpFolder.testUserPreferencesDataStore(
//            coroutineScope = scope,
//            userPreferencesSerializer = userPreferencesSerializer,
//        )
}

fun TemporaryFolder.testUserPreferencesDataStore(
    coroutineScope: CoroutineScope,
    userPreferencesSerializer: UserPreferencesSerializer = UserPreferencesSerializer(),
) = DataStoreFactory.create(
    serializer = userPreferencesSerializer,
    scope = coroutineScope,
) {
    newFile("user_preferences_test.pb")
}
