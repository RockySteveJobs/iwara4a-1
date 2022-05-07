package com.rerere.iwara4a.ui.component.md

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity

// 一加真是太帅拉!
// https://issuetracker.google.com/issues/231707291
@Composable
fun SliderPatch(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    val density = LocalDensity.current
    val initValue = remember { density }
    CompositionLocalProvider(
        LocalDensity provides initValue,
    ) {
        Slider(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished
        )
    }
}