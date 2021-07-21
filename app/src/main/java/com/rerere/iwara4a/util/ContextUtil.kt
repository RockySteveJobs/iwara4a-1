package com.rerere.iwara4a.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import com.rerere.iwara4a.model.index.MediaType

fun Context.vibrate(length: Long = 100L) {
    val service = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (service.hasVibrator()) {
        service.vibrate(
            VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE)
        )
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
        Toast.makeText(this, "打开链接失败: ${e.javaClass.simpleName}(${e.localizedMessage})", Toast.LENGTH_SHORT).show()
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