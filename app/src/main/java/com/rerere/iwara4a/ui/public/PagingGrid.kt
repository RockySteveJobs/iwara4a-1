package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.flow.asFlow

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
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
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    items(
        count = items.itemCount
    ) { index ->
        val item = items[index]
        itemContent(index, item)
    }
}