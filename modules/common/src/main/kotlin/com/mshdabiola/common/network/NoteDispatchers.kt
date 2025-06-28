/*
 *abiola 2024
 */

package com.mshdabiola.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val notepadDispatcher: NoteDispatchers)

enum class NoteDispatchers {
    Default,
    IO,
}
