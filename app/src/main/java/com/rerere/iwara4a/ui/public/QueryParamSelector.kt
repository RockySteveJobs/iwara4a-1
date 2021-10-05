package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.rerere.iwara4a.model.index.*
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun QueryParamSelector(
    queryParam: MediaQueryParam,
    onChangeSort: (sort: SortType) -> Unit,
    onChangeFilters: (filters: MutableSet<String>) -> Unit = {}
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
                    DropdownMenuItem(onClick = {
                        onChangeSort(it)
                        expand = false
                    }) {
                        Text(text = it.displayName())
                    }
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
                            RoundedChip(
                                selected = queryParam.filters.contains("${filter.type}:$value"),
                                onClick = {
                                    queryParam.filters.apply {
                                        if (contains("${filter.type}:$value")) {
                                            remove("${filter.type}:$value")
                                        } else {
                                            add("${filter.type}:$value")
                                        }
                                    }
                                    onChangeFilters(
                                        queryParam.filters
                                    )
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