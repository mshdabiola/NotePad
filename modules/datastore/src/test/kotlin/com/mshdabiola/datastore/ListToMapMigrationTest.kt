/*
 *abiola 2024
 */

package com.mshdabiola.datastore

class ListToMapMigrationTest {
//
//    @Test
//    fun ListToMapMigration_should_migrate_topic_ids() = runTest {
//        // Set up existing preferences with topic ids
//        val preMigrationUserPreferences = userPreferences {
//            deprecatedFollowedTopicIds.addAll(listOf("1", "2", "3"))
//        }
//        // Assert that there are no topic ids in the map yet
//        assertEquals(
//            emptyMap<String, Boolean>(),
//            preMigrationUserPreferences.followedTopicIdsMap,
//        )
//
//        // Run the migration
//        val postMigrationUserPreferences =
//            ListToMapMigration.migrate(preMigrationUserPreferences)
//
//        // Assert the deprecated topic ids have been migrated to the topic ids map
//        assertEquals(
//            mapOf("1" to true, "2" to true, "3" to true),
//            postMigrationUserPreferences.followedTopicIdsMap,
//        )
//
//        // Assert that the migration has been marked complete
//        assertTrue(postMigrationUserPreferences.hasDoneListToMapMigration)
//    }
//
//    @Test
//    fun ListToMapMigration_should_migrate_author_ids() = runTest {
//        // Set up existing preferences with author ids
//        val preMigrationUserPreferences = userPreferences {
//            deprecatedFollowedAuthorIds.addAll(listOf("4", "5", "6"))
//        }
//        // Assert that there are no author ids in the map yet
//        assertEquals(
//            emptyMap<String, Boolean>(),
//            preMigrationUserPreferences.followedAuthorIdsMap,
//        )
//
//        // Run the migration
//        val postMigrationUserPreferences =
//            ListToMapMigration.migrate(preMigrationUserPreferences)
//
//        // Assert the deprecated author ids have been migrated to the author ids map
//        assertEquals(
//            mapOf("4" to true, "5" to true, "6" to true),
//            postMigrationUserPreferences.followedAuthorIdsMap,
//        )
//
//        // Assert that the migration has been marked complete
//        assertTrue(postMigrationUserPreferences.hasDoneListToMapMigration)
//    }
}
