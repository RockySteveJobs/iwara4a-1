package com.rerere.iwara4a.ui.component.paging3

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyGridScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount
    ) { index ->
        itemContent(items[index])
    }
}

fun <T : Any> LazyGridScope.itemsIndexed(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyGridScope.(index: Int, value: T?) -> Unit
) {
    items(
        count = items.itemCount
    ) { index ->
        val item = items[index]
        itemContent(index, item)
    }
}