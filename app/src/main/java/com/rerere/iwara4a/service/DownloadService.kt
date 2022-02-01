package com.rerere.iwara4a.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class DownloadService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder = DownloadBinder()

    inner class DownloadBinder: Binder() {
        
    }
}