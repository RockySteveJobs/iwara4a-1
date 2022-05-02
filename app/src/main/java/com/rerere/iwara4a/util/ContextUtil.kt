package com.rerere.iwara4a.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import com.rerere.iwara4a.model.index.MediaType
import java.io.File

/**
 * 快速创建通知渠道
 *
 * @param channelId 渠道ID
 * @param name 渠道名称
 * @param importance 渠道通知的重要程度
 * @param description 渠道描述
 */
@SuppressLint("WrongConstant")
fun Context.createNotificationChannel(
    channelId: String,
    name: String,
    importance: Int = 2, // 2 = Low
    description: String? = null,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(
                NotificationChannel(
                    channelId,
                    name,
                    importance
                ).apply {
                    description?.let {
                        this.description = description
                    }
                    setSound(null, null)
                }
            )
        }
    }
}

/**
 * 判断是否允许自动旋转
 */
val Context.autoRotation: Boolean
    get() = try {
        Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

/**
 * 获取字符串资源
 */
fun Context.stringResource(id: Int) = this.resources.getString(id)

/**
 * 获取字符串资源
 */
fun Context.stringResource(id: Int, vararg formatArgs: Any) =
    this.resources.getString(id, *formatArgs)

/**
 * 打开网页
 */
fun Context.openUrl(url: String) {
    Toast.makeText(this, "打开链接: $url", Toast.LENGTH_SHORT).show()
    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url.let {
                if (!it.startsWith("https://")) "https://$it" else it
            })
        )
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            this,
            "打开链接失败: ${e.javaClass.simpleName}(${e.localizedMessage})",
            Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * 分享
 */
fun Context.shareMedia(mediaType: MediaType, mediaId: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "https://ecchi.iwara.tv/${mediaType.value}/$mediaId")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun Context.setClipboard(text: String) {
    val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
    Toast.makeText(this, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
}

fun Context.getVersionName(): String {
    var versionName = ""
    try {
        //获取软件版本号，对应AndroidManifest.xml下android:versionName
        versionName = this.packageManager.getPackageInfo(this.packageName, 0).versionName;
    } catch (e: Exception) {
        e.printStackTrace();
    }
    return versionName;
}

// 判断网络是否是免费网络 (什么鬼名字)
// 总之反正理解一下
fun Context.isFreeNetwork(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val activeNetwork = connectivityManager?.activeNetwork
    val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
    return networkCapabilities?.let {
        when {
            // WIFI
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            // 以太网
            it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            // 蜂窝
            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> false
            // 未知
            else -> true
        }
    } ?: true
}

fun Context.downloadImageNew(filename: String, downloadUrlOfImage: String) {
    try {
        val dm: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(downloadUrlOfImage)
        val request: DownloadManager.Request = DownloadManager.Request(downloadUri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle(filename)
            .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                File.separator.toString() + filename + ".jpg"
            )
        dm.enqueue(request)
        Toast.makeText(this, "开始保存图片", Toast.LENGTH_SHORT).show()
    } catch (e: java.lang.Exception) {
        Toast.makeText(this, "保存图片失败: ${e.javaClass.simpleName}", Toast.LENGTH_SHORT).show()
    }
}