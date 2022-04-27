package com.rerere.iwara4a.util

import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.R
import kotlin.system.exitProcess

private const val TAG = "CrashHandler"

class CrashHandler : Thread.UncaughtExceptionHandler {
    init {
        AppContext.instance.createNotificationChannel(
            channelId = "error",
            name = "崩溃",
            importance = 5,
            description = "崩溃日志"
        )
    }

    override fun uncaughtException(p0: Thread, p1: Throwable) {
        Log.i(TAG, "uncaughtException: ${p1.printStackTrace()}")

        val notify = NotificationCompat.Builder(AppContext.instance, "error")
            .setContentTitle("APP崩溃了")
            .setContentText(
                p1.stackTraceToString()
            )
            .setSmallIcon(R.drawable.miku)
            .setStyle(NotificationCompat.BigTextStyle())
            .build()

        AppContext.instance.getSystemService(NotificationManager::class.java)
            .notify(1, notify)

        exitProcess(0)
    }
}