package com.rerere.iwara4a.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.callback.DownloadManager
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.R
import com.rerere.iwara4a.util.createNotificationChannel

private const val NOTIFY_ID = 1

class DownloadService : Service() {
    private val downloadManager = DownloadService.getDownloadManager(AppContext.instance)
    private val notificationBuilder = NotificationCompat.Builder(this, "download")
        .setSmallIcon(R.drawable.ic_far_save)
        .setContentTitle("下载视频")
        .setContentText("")
        .setProgress(
            0,0, true
        )

    override fun onCreate() {
        createNotificationChannel("download","下载")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val url = intent.getStringExtra("url")

        startForeground(NOTIFY_ID, notificationBuilder.build())

        return super.onStartCommand(intent, flags, startId)
    }

    private val binder = DownloadBinder()

    inner class DownloadBinder : Binder() {

    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}