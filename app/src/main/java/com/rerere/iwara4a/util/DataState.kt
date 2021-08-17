package com.rerere.iwara4a.util

sealed class DataState<out T> {
    object Empty : DataState<Nothing>()
    object Loading : DataState<Nothing>()

    data class Error(
        val message: String
    ) : DataState<Nothing>()

    data class Success<T>(
        val data: T
    ) : DataState<T>()

    fun read() : T = (this as Success<T>).data

    fun readSafely() : T? = if(this is Success<T>) read() else null
}