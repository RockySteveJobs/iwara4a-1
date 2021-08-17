package com.rerere.iwara4a.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import com.rerere.iwara4a.model.index.MediaType

fun Context.vibrate(length: Long = 100L) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = manager.defaultVibrator
        vibrator.vibrate(VibrationEffect.createOneShot(length, VibrationEffect.EFFECT_HEAVY_CLICK))
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val service = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (service.hasVibrator()) {
            service.vibrate(
                VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    } else {
        val service = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        service.takeIf {
            it.hasVibrator()
        }?.vibrate(length)
    }
}

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