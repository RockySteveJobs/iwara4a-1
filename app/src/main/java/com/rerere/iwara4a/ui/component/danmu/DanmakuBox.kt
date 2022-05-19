package com.rerere.iwara4a.ui.component.danmu

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Stable
class DanmakuState {
    private val danmakuList = arrayListOf<Danmaku>()

    fun addDanmaku(danmaku: Danmaku) {
        danmakuList += danmaku
    }
}

@Composable
fun DanmakuBox(
    modifier: Modifier = Modifier,
    state: DanmakuState,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .wrapContentSize(),
        contentAlignment = Alignment.TopStart
    ) {
        content()
    }
}
