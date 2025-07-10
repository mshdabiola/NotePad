/*
 *abiola 2022
 */

package com.mshdabiola.playnotepad.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.mshdabiola.data.util.NetworkMonitor
import com.mshdabiola.main.navigation.Main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Composable
fun rememberNoteAppState(
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavBackStack = rememberNavBackStack(Main),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
): NoteAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
        drawerState,
    ) {
        NoteAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            drawerState = drawerState,
        )
    }
}

@Stable
class NoteAppState(
    val navController: NavBackStack,
    val coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    val drawerState: DrawerState,
) {
    val currentRoute = snapshotFlow { navController.toList() }
        .map { it.lastOrNull() }

    val isMain = currentRoute
        .map { it == Main }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun closeDrawer() {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    fun openDrawer() {
        coroutineScope.launch {
            drawerState.open()
        }
    }
}

fun NavBackStack.pop() {
    removeAt(lastIndex)
}
