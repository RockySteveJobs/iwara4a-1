package com.rerere.iwara4a.util

import android.util.Log
import android.widget.Toast
import com.rerere.iwara4a.AppContext

private const val TAG = "CrashHandler"

class CrashHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(p0: Thread, p1: Throwable) {
        Log.i(TAG, "uncaughtException: ${p1.printStackTrace()}")
        Toast.makeText(AppContext.instance, "Error: ${p1.stackTraceToString()}", Toast.LENGTH_SHORT).show()
    }
}