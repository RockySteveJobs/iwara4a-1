package com.rerere.iwara4a.util

import android.content.Context
import android.content.Intent
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.service.DownloadService

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
}