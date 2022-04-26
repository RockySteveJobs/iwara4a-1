package com.rerere.iwara4a.ui.component.paging3

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    content: @Composable (T?) -> Unit
) {
    items(
        count = items.itemCount
    ) { index ->
        content(items[index])
    }
}