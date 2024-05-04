/*
 *abiola 2024
 */

package com.mshdabiola.datastore

import androidx.datastore.core.DataMigration

/**
 * Migrates saved ids from [Int] to [String] types
 */
internal object IntToStringIdsMigration : DataMigration<UserPreferences> {

    override suspend fun cleanUp() = Unit

    override suspend fun migrate(currentData: UserPreferences): UserPreferences =
        currentData.copy {
            // Migrate topic ids

        }

    override suspend fun shouldMigrate(currentData: UserPreferences): Boolean =
        true
}
