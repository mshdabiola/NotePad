/*
 *abiola 2024
 */

package com.mshdabiola.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val niaDispatcher: SkDispatchers)

enum class SkDispatchers {
    Default,
    IO,
}
