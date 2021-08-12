package com.rerere.iwara4a.util

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.model.download.DownloadingVideo
import com.rerere.iwara4a.ui.screen.download.DownloadingList
import com.rerere.iwara4a.util.okhttp.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

private const val TAG = "DownloadUtil"

private val httpClient by lazy {
    OkHttpClient.Builder()
        .build()
}

var downloadingList = listOf<DownloadingVideo>()

fun Long.toFileSize() : String {
    return "${(this / 1024f / 1024f).format()} MB"
}

@Composable
fun isDownloaded(videoDetail: VideoDetail) : Boolean {
    val context = LocalContext.current
    return File(
        context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
        videoDetail.nid.toString()
    ).exists().also {
        println("!!! CHECKING FILE !!!")
    }
}

fun Context.downloadVideo(videoDetail: VideoDetail, url: String) {
    if (downloadingList.any {
            it.nid == videoDetail.nid
        }) {
        Toast.makeText(this, "已经开始下载该视频了！", Toast.LENGTH_SHORT).show()
    }

    Log.i(TAG, "downloadVideo: Start download video (${videoDetail.nid}) from $url")
    AppContext.applicationScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val database = AppContext.database.getDownloadedVideoDao()
                val file = File(
                    getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                    videoDetail.nid.toString()
                ).apply {
                    if (exists()) {
                        delete()
                    }
                    createNewFile()
                }

                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = httpClient.newCall(request).await()
                require(response.isSuccessful)

                repeat(response.headers.size) {
                    Log.i(
                        TAG,
                        "downloadVideo: Header -> ${response.headers.name(it)}: ${
                            response.headers.value(it)
                        }"
                    )
                }

                Log.i(TAG, "downloadVideo: Start wrinting file...")
                response.body?.byteStream()
                    ?.use {
                        val total = response.headers["Content-Length"]!!.toLong()

                        val downloadingVideo = DownloadingVideo(
                            nid = videoDetail.nid,
                            title = videoDetail.title,
                            fileName = file.name,
                            downloadDate = System.currentTimeMillis(),
                            preview = videoDetail.preview
                        )
                        downloadingList = downloadingList + downloadingVideo
                        DownloadingList.downloading.apply {
                            // send(emptyList())
                            send(downloadingList)
                        }

                        var sum = 0
                        val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
                        val outputStream = file.outputStream()
                        var lastPercent = 0f
                        while (true) {
                            val length = it.read(bytes)
                            if (length == -1) {
                                break
                            }
                            sum += length
                            outputStream.write(bytes, 0, length)
                            val percent = (sum.toDouble() / total).toFloat()
                            if (abs(percent - lastPercent) >= 0.01) {
                                lastPercent = percent

                                downloadingVideo.progress = percent
                                DownloadingList.downloading.apply {
                                    send(emptyList())
                                    send(downloadingList)
                                }

                                println("Update Progress = $percent")
                            }
                        }
                        outputStream.apply {
                            flush()
                            close()
                        }
                        downloadingList = downloadingList - downloadingVideo
                        DownloadingList.downloading.apply {
                            // send(emptyList())
                            send(downloadingList)
                        }
                        database.apply {
                            if (getVideo(downloadingVideo.nid) == null) {
                                insertVideo(
                                    DownloadedVideo(
                                        nid = downloadingVideo.nid,
                                        title = downloadingVideo.title,
                                        fileName = downloadingVideo.fileName,
                                        downloadDate = downloadingVideo.downloadDate,
                                        preview = downloadingVideo.preview,
                                        size = total
                                    )
                                )
                            }
                        }
                        Log.i(
                            TAG,
                            "downloadVideo: Download complete! (cur: ${downloadingList.size})"
                        )
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}