/*
 *abiola 2024
 */

package com.mshdabiola.detail

sealed class DetailState {

    data class Loading(val isLoading: Boolean = false) : DetailState()
    data class Success(
        val id: Long,
    ) : DetailState()

    data class Error(val exception: Throwable) : DetailState()
}

fun DetailState.getSuccess(value: (DetailState.Success) -> DetailState.Success): DetailState {
    return if (this is DetailState.Success) {
        value(this)
    } else {
        this
    }
}
