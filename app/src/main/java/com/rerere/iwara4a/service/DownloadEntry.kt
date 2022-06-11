package com.rerere.iwara4a.service

/**
 * 代表一个下载任务
 *
 * @param url 下载地址
 * @param nid 视频NID
 * @param title 视频标题
 * @param preview 预览封面
 */
data class DownloadEntry(
    val url: String,
    val nid: Int,
    val title: String,
    val preview: String
)