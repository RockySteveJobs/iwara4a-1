package com.rerere.iwara4a.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
private val sdf = SimpleDateFormat("yyyy-MM-dd")

@SuppressLint("SimpleDateFormat")
private val sdfDetail = SimpleDateFormat("yyy-MM-dd HH:mm")

fun Long.format(
    detail: Boolean = false
): String = if (detail) sdfDetail.format(Date(this)) else sdf.format(Date(this))

fun prettyDuration(duration: Long): String {
    val seconds = duration / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        days > 0 -> "${days}:${(hours % 24).toString().padStart(2, '0')}:${(minutes % 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
        hours > 0 -> "${hours}:${(minutes % 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')}"
        else -> "${minutes}:${(seconds % 60).toString().padStart(2, '0')}"
    }
}