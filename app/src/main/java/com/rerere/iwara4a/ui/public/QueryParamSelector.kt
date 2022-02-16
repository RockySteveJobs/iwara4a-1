package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun rememberMediaQueryParamState() = rememberSaveable {
    MediaQueryParam(SortType.DATE, hashSetOf())
}

class MediaQueryParam(
    var sortType: SortType,
    var filters: MutableSet<MediaFilter>
)

enum class SortType(val value: String) {
    DATE("date"),
    VIEWS("views"),
    LIKES("likes")
}

data class MediaFilter(
    val type: String,
    val value: List<String>
) {
    constructor(type: String, vararg values: String) : this(type, values.toList())
}

val MEDIA_FILTERS = listOf(
    MediaFilter("type", "video", "image"),
    MediaFilter("created", "2021", "2020", "2019", "2018"),
    MediaFilter("field_categories", "6", "16190", "31264")
)

@Composable
fun Pair<String, String>.filterName() = when (this.first) {
    "type" -> "类型: " + when (this.second) {
        "video" -> "视频"
        "image" -> "图片"
        else -> this.second
    }
    "created" -> "上传日期: ${this.second}"
    "field_categories" -> "类型: " + when (this.second) {
        "6" -> "Vocaloid"
        "16190" -> "虚拟主播"
        "31264" -> "原神"
        else -> "(${this.second})"
    }
    else -> "${this.first}: ${this.second}"
}

@Composable
fun SortType.displayName() = when (this) {
    SortType.DATE -> "日期"
    SortType.VIEWS -> "播放量"
    SortType.LIKES -> "喜欢"
}

@Composable
fun QueryParamSelector(
    queryParam: MediaQueryParam
) {
    val sortDialog = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = sortDialog,
        buttons = {
            button("确定") {
                sortDialog.hide()
            }
        }
    ) {
        title(text = "排序和过滤")
        customView {
            var expand by remember {
                mutableStateOf(false)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(0.4f), text = "排序条件")
                Row(
                    modifier = Modifier
                        .weight(0.6f)
                        .clickable {
                            expand = !expand
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = queryParam.sortType.displayName(), modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            }
            DropdownMenu(
                expanded = expand,
                onDismissRequest = {
                    expand = false
                },
                offset = DpOffset(
                    x = 120.dp,
                    y = 0.dp
                )
            ) {
                SortType.values().forEach {
                    DropdownMenuItem(
                        onClick = {
                            // TODO
                        },
                        text = {
                            Text(text = it.displayName())
                        }
                    )
                }
            }
        }
        customView {
            Column {
                Text(text = "过滤条件")
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    MEDIA_FILTERS.forEach { filter ->
                        filter.value.forEach { value ->
                            FilledTonalButton(
                                onClick = {
                                   // TODO
                                }
                            ) {
                                Text(text = (filter.type to value).filterName())
                            }
                        }
                    }
                }
            }
        }
    }

    IconButton(onClick = { sortDialog.show() }) {
        Icon(Icons.Default.MenuOpen, null)
    }
}