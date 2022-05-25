package com.rerere.iwara4a.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.task.DownloadTask
import com.google.gson.Gson
import com.rerere.iwara4a.R
import com.rerere.iwara4a.data.dao.AppDatabase
import com.rerere.iwara4a.data.model.download.DownloadedVideo
import com.rerere.iwara4a.ui.activity.RouterActivity
import com.rerere.iwara4a.util.createNotificationChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

private const val TAG = "DownloadService"

@AndroidEntryPoint
class DownloadService : Service() {
    @Inject
    lateinit var database: AppDatabase

    // Gson
    private val gson = Gson()

    // 协程 Scope
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // 下载进度通知
    private val dNotification = NotificationCompat.Builder(this, "download")
        .setSmallIcon(R.drawable.download)
        .setContentTitle("Downloading")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

    // 下载完成通知
    private val fNotification = NotificationCompat.Builder(this, "download")
        .setSmallIcon(R.drawable.download)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentTitle("Download Finished")

    override fun onCreate() {
        super.onCreate()

        // 注册 Aria
        Aria.download(this).register()
        Aria.get(this).downloadConfig.isConvertSpeed = true

        // 创建通知渠道
        createNotificationChannel("download", "download")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 从 Intent 中获取下载信息
        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")
        val nid = intent.getIntExtra("nid", 0)
        val preview = intent.getStringExtra("preview")
        
        if(title == null || url == null || preview == null) return START_NOT_STICKY

        if (Aria.download(this).getDownloadEntity(url) != null) {
            // 已经在下载队列中
            Log.i(TAG, "onStartCommand: Already downloading: $title")
        } else {
            // 移除已经下载的视频记录
            scope.launch {
                database.getDownloadedVideoDao().getVideo(nid)?.let {
                    database.getDownloadedVideoDao().delete(it)
                }
            }

            // 指定下载路径
            val file = File(
                getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                nid.toString()
            ).apply {
                if(exists()){
                    delete()
                }
            }

            // 开始下载
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

    override fun onBind(p0: Intent?): IBinder {
        return DownloadBinder()
    }

    @Download.onTaskStart
    fun onStart(task: DownloadTask){
        dNotification
            .setContentText("Downloading ${Aria.download(this).dRunningTask.size} Files")
            .setProgress(100, Aria.download(this).dRunningTask.map { it.percent }.average().toInt(), false)
            .build().apply {
                startForeground(1, this)
            }
    }

    @Download.onTaskComplete
    fun onComplete(task: DownloadTask) {
        val entry = gson.fromJson(task.extendField, DownloadEntry::class.java)
        // 加入数据库记录
        scope.launch(Dispatchers.IO) {
            database.getDownloadedVideoDao()
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
        // 通知下载已经完成
        notificationManager.notify(
            2,
            fNotification
                .setContentText(entry.title)
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, RouterActivity::class.java).apply {
                             data = Uri.parse("iwara4a://download")
                        },
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
                .build()
        )
        // 判断剩余 Task
        if(Aria.download(this).dRunningTask == null){
            // 已全部下载完成, 停止前台服务
            stopForeground(true)
        }
    }

    @Download.onTaskRunning
    fun onRunning(task: DownloadTask) {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 更新下载进度
        dNotification
            .setContentText("Downloading ${Aria.download(this).dRunningTask.size} Files | ${task.convertSpeed}")
            .setProgress(100, Aria.download(this).dRunningTask.map { it.percent }.average().toInt(), false)
            .build().apply {
                notificationManager.notify(1, this)
            }
    }

    inner class DownloadBinder: Binder() {
        fun getDownloadingTasks(): List<RunningTask> {
            val runningTasks = Aria.download(this).dRunningTask ?: emptyList()
            return runningTasks.map {
                RunningTask(
                    it
                )
            }
        }
    }
}

data class RunningTask(
    val entity: DownloadEntity
)