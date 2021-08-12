package com.rerere.iwara4a.model.download

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DownloadedVideo(
    @PrimaryKey val nid: Int,
    val fileName: String,
    val title: String,
    val downloadDate: Long,
    val preview: String,
    @ColumnInfo(defaultValue = "0") val size: Long
)