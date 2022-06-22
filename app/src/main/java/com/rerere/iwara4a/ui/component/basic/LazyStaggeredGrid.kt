package com.rerere.iwara4a.ui.component.basic

import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 一个基于多重LazyColumn的StaggeredGrid实现
 *
 * [Github](https://github.com/savvasdalkitsis/lazy-staggered-grid)
 */
@Composable
fun LazyStaggeredGrid(
    columnCount: Int,
    states: List<LazyListState> = List(columnCount) { rememberLazyListState() },
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyStaggeredGridScope.() -> Unit,
) {
    check(columnCount == states.size) {
        "Invalid number of lazy list states. Expected: $columnCount. Actual: ${states.size}"
    }

    val scope = rememberCoroutineScope { Dispatchers.Main.immediate }
    val scrollConnections = List(columnCount) { index ->
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                scope.launch {
                    states.forEachIndexed { stateIndex, state ->
                        if (stateIndex != index) {
                            state.scrollBy(-delta)
                        }
                    }
                }
                return Offset.Zero
            }
        }
    }
    val gridScope = RealLazyStaggeredGridScope(columnCount).apply(content)

    // Disable overscroll otherwise it'll only overscroll one column.
    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Row {
            for (index in 0 until columnCount) {
                LazyColumn(
                    contentPadding = contentPadding,
                    state = states[index],
                    modifier = Modifier
                        .nestedScroll(scrollConnections[index])
                        .weight(1f)
                ) {
                    for ((key, itemContent) in gridScope.items[index]) {
                        item(key) { itemContent() }
                    }
                }
            }
        }
    }
}

/** Receiver scope which is used by [LazyStaggeredGrid]. */
interface LazyStaggeredGridScope {

    /** Adds a single item. */
    fun item(
        key: Any? = null,
        content: @Composable () -> Unit
    )

    /** Adds a [count] of items. */
    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable (index: Int) -> Unit
    )
}

/** Adds a list of items. */
inline fun <T> LazyStaggeredGridScope.items(
    items: List<T>,
    noinline key: ((item: T) -> Any)? = null,
    crossinline itemContent: @Composable (item: T) -> Unit
) = items(
    count = items.size,
    key = if (key != null) { index -> key(items[index]) } else null,
    itemContent = { itemContent(items[it]) }
)

private class RealLazyStaggeredGridScope(private val columnCount: Int) : LazyStaggeredGridScope {
    val items = Array(columnCount) { mutableListOf<Pair<Any?, @Composable () -> Unit>>() }
    var currentIndex = 0

    override fun item(key: Any?, content: @Composable () -> Unit) {
        items[currentIndex % columnCount] += key to content
        currentIndex += 1
    }

    override fun items(count: Int, key: ((Int) -> Any)?, itemContent: @Composable (Int) -> Unit) {
        for (index in 0 until count) {
            item(key?.invoke(index)) { itemContent(index) }
        }
    }
}