package com.rerere.iwara4a.util

import androidx.compose.runtime.Composable

sealed class DataState<out T> {
    object Empty : DataState<Nothing>()
    object Loading : DataState<Nothing>()

    data class Error(
        val message: String
    ) : DataState<Nothing>()

    data class Success<T>(
        val data: T
    ) : DataState<T>()

    fun read(): T = (this as Success<T>).data

    fun readSafely(): T? = if (this is Success<T>) read() else null
}

@Composable
inline fun <T> DataState<T>.onSuccess(
    content: @Composable ((T) -> Unit)
): DataState<T> {
    if (this is DataState.Success) {
        content(this.data)
    }
    return this
}

@Composable
inline fun <T> DataState<T>.onError(
    content: @Composable ((String) -> Unit)
): DataState<T> {
    if (this is DataState.Error) {
        content(this.message)
    }
    return this
}

@Composable
inline fun <T> DataState<T>.onLoading(
    content: @Composable (() -> Unit)
): DataState<T> {
    if (this is DataState.Loading) {
        content()
    }
    return this
}