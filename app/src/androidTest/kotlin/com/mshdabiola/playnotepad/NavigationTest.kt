/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Tests all the navigation flows that are handled by the navigation library.
 */
@HiltAndroidTest
class NavigationTest {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Create a temporary folder used to create a Data Store file. This guarantees that
     * the file is removed in between each test, preventing a crash.
     */
    @BindValue
    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    /**
     * Grant [android.Manifest.permission.POST_NOTIFICATIONS] permission.
     */
//    @get:Rule(order = 2)
//    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

//    private fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
//        ReadOnlyProperty<Any, String> { _, _ -> activity.getString(resId) }

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun firstScreen_isMain() {
        composeTestRule.apply {
            // VERIFY for you is selected
            onNodeWithTag("main:add").assertExists()
        }
    }

    @Test
    fun onAddButton_showDetails() {
        composeTestRule.apply {
            // GIVEN the user follows a topic
            onNodeWithTag("main:add").performClick()

            // onNodeWithTag("detail:title").assertExists()
        }
    }
}
