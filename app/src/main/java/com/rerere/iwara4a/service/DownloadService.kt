package com.rerere.iwara4a.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.google.gson.Gson
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.ui.activity.RouterActivity
import kotlinx.coroutines.*
import java.io.File

private const val TAG = "DownloadService"

class DownloadService : Service() {
    private val gson = Gson()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val dNotification = NotificationCompat.Builder(this, "download")
        .setSmallIcon(R.drawable.download)
        .setContentTitle("Downloading")
    private val fNotification = NotificationCompat.Builder(this, "download")
        .setSmallIcon(R.drawable.download)
        .setContentTitle("Finished")

    override fun onCreate() {
        super.onCreate()
        Aria.download(this).register()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val title = intent.getStringExtra("title")!!
        val url = intent.getStringExtra("url")!!
        val nid = intent.getIntExtra("nid", 0)
        val preview = intent.getStringExtra("preview")!!

        if (Aria.download(this).getDownloadEntity(url) != null) {
            Log.i(TAG, "onStartCommand: Already downloading: $title")
        } else {
            scope.launch {
                AppContext.database.getDownloadedVideoDao().getVideo(nid)?.let {
                    AppContext.database.getDownloadedVideoDao().delete(it)
                }
            }
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                nid.toString()
            ).apply {
                if(exists()){
                    delete()
                }
            }
            Aria.download(this)
                .load(url)
                .setFilePath(file.path)
                .setExtendField(
                    gson.toJson(
                        DownloadEntry(
                            title = title,
                            url = url,
                            nid = nid,
                            preview = preview
                        )
                    )
                )
                .ignoreFilePathOccupy()
                .ignoreCheckPermissions()
                .create()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        Aria.download(this).unRegister()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @Download.onTaskComplete
    fun onComplete(task: DownloadTask) {
        val entry = gson.fromJson(task.extendField, DownloadEntry::class.java)
        Toast.makeText(AppContext.instance, "下载完成: ${entry.title}", Toast.LENGTH_SHORT).show()
        scope.launch(Dispatchers.IO) {
            AppContext.database.getDownloadedVideoDao()
                .insertVideo(
                    DownloadedVideo(
                        nid = entry.nid,
                        title = entry.title,
                        downloadDate = System.currentTimeMillis(),
                        fileName = entry.nid.toString(),
                        size = task.fileSize,
                        preview = entry.preview
                    )
                )
        }
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
        notificationManager.notify(
            2,
            fNotification
                .setContentText(entry.title)
                .setContentIntent(
                    PendingIntent.getActivity(this, 0, Intent(this, RouterActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
                )
                .build()
        )
    }

    @Download.onTaskRunning
    fun onRunning(task: DownloadTask) {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        dNotification
            .setContentText("Downloading ${Aria.download(this).dRunningTask.size} Files")
            .setProgress(100, Aria.download(this).dRunningTask.map { it.percent }.average().toInt(), false)
            .build().apply {
                notificationManager.notify(1, this)
            }
    }
}