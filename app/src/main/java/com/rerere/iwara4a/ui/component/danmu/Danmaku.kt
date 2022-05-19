package com.rerere.iwara4a.ui.component.danmu

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class Danmaku(
    val position: Long
) {
    abstract fun display(): @Composable () -> Unit
}

class BasicDanmaku(
    val text: String,
    position: Long
) : Danmaku(position) {
    override fun display(): @Composable () -> Unit = {
        Text(text)
    }
}
