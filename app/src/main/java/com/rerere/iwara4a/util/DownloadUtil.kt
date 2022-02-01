package com.rerere.iwara4a.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.model.download.DownloadingVideo
import com.rerere.iwara4a.service.DownloadService
import com.rerere.iwara4a.ui.screen.download.DownloadingList
import com.rerere.iwara4a.util.okhttp.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.abs

private const val TAG = "DownloadUtil"

fun Long.toFileSize(): String {
    return "${(this / 1024f / 1024f).format()} MB"
}

fun Context.downloadVideo(videoDetail: VideoDetail, url: String) {
    startService(Intent(
        this,
        DownloadService::class.java
    ).apply {
        putExtra("nid", videoDetail.nid)
        putExtra("title", videoDetail.title)
        putExtra("url", url)
        putExtra("preview", videoDetail.preview)
    })
    return
}