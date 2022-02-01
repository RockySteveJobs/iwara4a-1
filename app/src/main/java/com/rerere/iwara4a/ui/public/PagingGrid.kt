package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyGridScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount
    ) { index ->
        val item = items[index]
        itemContent(item)
    }
}

@OptIn(ExperimentalFoundationApi::class)
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