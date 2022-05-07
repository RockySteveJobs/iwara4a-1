package com.rerere.iwara4a.util

import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.google.gson.Gson

private val gson = Gson()

data class LogEntry(
    val time: Long = System.currentTimeMillis(),
    val level: Int = LogLevel.INFO,
    val tag: String = "iwara4a",
    val thread: String = Thread.currentThread().name,
    val message: String = "null"
) {
    override fun toString(): String {
        return gson.toJson(this)
    }

    companion object {
        fun fromString(s: String): LogEntry {
            return gson.fromJson(s, LogEntry::class.java)
        }
    }
}

fun logInfo(message: String) {
    XLog.i(message)
}

fun logError(message: String = "", throwable: Throwable) {
    XLog.e(message, throwable)
}