package com.rerere.iwara4a.ui.util

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.util.findActivity

@Composable
fun adaptiveGridCell(): GridCells {
    val windowSizeClass = calculateWindowSizeClass(LocalContext.current.findActivity())
    return when(windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> GridCells.Fixed(2)
        else -> GridCells.Adaptive(200.dp)
    }
}