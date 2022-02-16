package com.rerere.iwara4a.ui.public

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

data class MediaQueryParam(
    var sortType: SortType,
    var filters: MutableSet<String>
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
    queryParam: MediaQueryParam,
    onChangeSort: (SortType) -> Unit,
    onChangeFiler: (MutableSet<String>) -> Unit,
    onClose: () -> Unit
) {
    val sortDialog = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = sortDialog,
        buttons = {
            button("确定") {
                sortDialog.hide()
                onClose()
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
                            onChangeSort(it)
                            expand = false
                        },
                        text = {
                            Text(text = it.displayName())
                        }
                    )
                }
            }
        }
        customView {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "过滤条件")
                MEDIA_FILTERS.forEach { filter ->
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        filter.value.forEach { value ->
                            val code = "${filter.type}:$value"
                            FilterChip(
                                selected = queryParam.filters.contains(code),
                                onClick = {
                                    if (!queryParam.filters.contains(code)) {
                                        queryParam.filters.add(code)
                                    } else {
                                        queryParam.filters.remove(code)
                                    }
                                    onChangeFiler(queryParam.filters.toMutableSet())
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

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.merge(TextStyle(fontSize = 12.sp))
    ) {
        Crossfade(
            targetState = selected
        ) {
            if (it) {
                Surface(
                    modifier = Modifier.clickable { onClick() },
                    shape = RoundedCornerShape(8.dp),
                    tonalElevation = 16.dp
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        content()
                    }
                }
            } else {
                OutlinedCard(
                    modifier = Modifier.clickable { onClick() },
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        content()
                    }
                }
            }
        }
    }
}