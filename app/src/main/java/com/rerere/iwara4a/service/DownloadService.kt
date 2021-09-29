package com.rerere.iwara4a.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class DownloadService : Service() {
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }
}