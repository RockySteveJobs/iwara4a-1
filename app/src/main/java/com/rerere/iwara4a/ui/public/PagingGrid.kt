package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount
    ) { index ->
        itemContent(items[index])
    }
}