/*
 *abiola 2022
 */

package com.mshdabiola.testing.util

import com.mshdabiola.data.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestNetworkMonitor : NetworkMonitor {

    private val connectivityFlow = MutableStateFlow(true)

    override val isOnline: Flow<Boolean> = connectivityFlow

    /**
     * A test-only API to set the connectivity state from tests.
     */
    fun setConnected(isConnected: Boolean) {
        connectivityFlow.value = isConnected
    }
}
