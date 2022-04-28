package com.rerere.iwara4a.util

import android.content.Intent
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.ui.activity.CrashActivity

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
        p1.printStackTrace()

        AppContext.instance.startActivity(
            Intent(AppContext.instance, CrashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("stackTrace", p1.stackTraceToString())
            }
        )

        /*val notify = NotificationCompat.Builder(AppContext.instance, "error")
            .setContentTitle("APP崩溃了")
            .setContentText(
                p1.stackTraceToString()
            )
            .setSmallIcon(R.drawable.miku)
            .setStyle(NotificationCompat.BigTextStyle())
            .build()

        AppContext.instance.getSystemService(NotificationManager::class.java)
            .notify(1, notify)

        exitProcess(0)*/
    }
}