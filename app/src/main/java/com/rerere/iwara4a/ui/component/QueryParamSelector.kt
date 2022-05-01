package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.MenuOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.google.accompanist.flowlayout.FlowRow
import com.rerere.iwara4a.R

data class MediaQueryParam(
    var sortType: SortType,
    var filters: MutableSet<String>
) {
    companion object {
        @JvmStatic
        val Default = MediaQueryParam(
            sortType = SortType.DATE,
            filters = hashSetOf()
        )
    }
}

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
    MediaFilter(
        "created",
        "2022-01",
        "2022-02",
        "2022-03",
        "2022-04",
        "2022",
        "2021",
        "2020",
        "2019",
        "2018"
    ),
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
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("排序和过滤")
            },
            text = {
                Column {
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
                            Text(
                                text = queryParam.sortType.displayName(),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Outlined.ArrowDropDown, null)
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
                    }

                    Column {
                        Text(text = "过滤条件")
                        MEDIA_FILTERS.fastForEach { filter ->
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                mainAxisSpacing = 4.dp
                            ) {
                                filter.value.fastForEach { value ->
                                    val code = "${filter.type}:$value"
                                    FilterChip(
                                        selected = queryParam.filters.contains(code),
                                        onClick = {
                                            val filters = queryParam.filters.toMutableSet()
                                            if (!filters.contains(code)) {
                                                filters.add(code)
                                            } else {
                                                filters.remove(code)
                                            }
                                            onChangeFiler(filters)
                                        },
                                        label = {
                                            Text(text = (filter.type to value).filterName())
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onClose()
                    }
                ) {
                    Text(stringResource(R.string.confirm_button))
                }
            }
        )
    }

    IconButton(onClick = { showDialog = true }) {
        Icon(Icons.Outlined.MenuOpen, null)
    }
}