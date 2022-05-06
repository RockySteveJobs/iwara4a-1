package com.rerere.iwara4a.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
private val sdf = SimpleDateFormat("yyyy-MM-dd")

@SuppressLint("SimpleDateFormat")
private val sdfDetail = SimpleDateFormat("yyy-MM-dd HH:mm")

/**
 * 将时间戳转换为日期
 *
 * @param detail 是否显示详细时间 (时分秒)
 * @return 格式化后的日期
 */
fun Long.format(
    detail: Boolean = false
): String {
    return if (detail) {
        sdfDetail.format(Date(this))
    } else {
        sdf.format(Date(this))
    }
}

/**
 * 将时间戳转换为时间, 格式为 00:00
 *
 * @param duration 时间长度(毫秒)
 * @return 格式化后的时间
 */
fun prettyDuration(duration: Long): String {
    val seconds = duration / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return when {
        hours > 0 -> "${hours.toString().padStart(2, '0')}:${
            (minutes % 60).toString().padStart(2, '0')
        }:${(seconds % 60).toString().padStart(2, '0')}"
        else -> "${minutes.toString().padStart(2, '0')}:${
            (seconds % 60).toString().padStart(2, '0')
        }"
    }
}