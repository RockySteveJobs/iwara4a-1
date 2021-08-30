package com.rerere.iwara4a.model.history

import androidx.compose.runtime.Composable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HistoryData(
    @PrimaryKey val date: Long,
    val route: String,
    val preview: String,
    val title: String,
    val historyType: HistoryType
)

enum class HistoryType {
    VIDEO,
    IMAGE,
    USER
}

@Composable
fun HistoryType.asString() = when (this) {
    HistoryType.VIDEO -> "视频"
    HistoryType.IMAGE -> "图片"
    HistoryType.USER -> "用户"
}