package com.core.meteoradar.util

sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val throwable: Throwable) : AppResult<Nothing>
    data object Loading : AppResult<Nothing>
}
