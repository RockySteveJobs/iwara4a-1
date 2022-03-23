package com.rerere.iwara4a.util

import android.content.Context
import android.content.Intent
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.service.DownloadService

@JvmInline
value class FileSize(
    private val bytes: Long
) {
    override fun toString(): String {
        return if (bytes < 1024) {
            "$bytes B"
        } else if (bytes < 1024 * 1024) {
            "${bytes / 1024} KB"
        } else if (bytes < 1024 * 1024 * 1024) {
            "${bytes / 1024 / 1024} MB"
        } else {
            "${bytes / 1024 / 1024 / 1024} GB"
        }
    }
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