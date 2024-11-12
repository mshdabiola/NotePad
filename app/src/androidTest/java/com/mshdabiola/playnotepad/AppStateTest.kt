/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.mshdabiola.playnotepad.ui.NoteAppState
import com.mshdabiola.testing.util.TestNetworkMonitor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class AppStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val networkMonitor = TestNetworkMonitor()

    // Subject under test.
    private lateinit var state: NoteAppState

    @Test
    fun niaAppState_currentDestination() = runTest {
        var currentDestination: String? = null

        composeTestRule.setContent {
            val navController = rememberTestNavController()
            state =
//                remember(navController) {
                NoteAppState(
                    navController = NavHostController(LocalContext.current),
                    coroutineScope = backgroundScope,
                    networkMonitor = networkMonitor,
                    drawerState = DrawerState(
                        initialValue = DrawerValue.Closed,
                        confirmStateChange = { true },
                    ),
                )
//            }

            // Update currentDestination whenever it changes
            currentDestination = state.currentRoute

            // Navigate to destination b once
            LaunchedEffect(Unit) {
                navController.setCurrentDestination("b")
            }
        }

        assertEquals("b", currentDestination)
    }

    @Test
    fun niaAppState_whenNetworkMonitorIsOffline_StateIsOffline() = runTest(UnconfinedTestDispatcher()) {
        composeTestRule.setContent {
            state = NoteAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                networkMonitor = networkMonitor,
                drawerState = DrawerState(
                    initialValue = DrawerValue.Closed,
                    confirmStateChange = { true },
                ),
            )
        }

        backgroundScope.launch { state.isOffline.collect() }
        networkMonitor.setConnected(false)
        assertEquals(
            true,
            state.isOffline.value,
        )
    }
}

@Composable
private fun rememberTestNavController(): TestNavHostController {
    val context = LocalContext.current
    return remember {
        TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = "a") {
                composable("a") { }
                composable("b") { }
                composable("c") { }
            }
        }
    }
}
