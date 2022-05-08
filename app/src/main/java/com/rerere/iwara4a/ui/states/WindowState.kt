package com.rerere.iwara4a.ui.states

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import com.rerere.iwara4a.util.findActivity

@Composable
fun rememberWindowSizeClass(): WindowSize {
    val activity = LocalContext.current.findActivity() as ComponentActivity
    var windowMetrics by remember {
        mutableStateOf(
            WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)
        )
    }
    DisposableEffect(Unit) {
        val listener: (Configuration) -> Unit = {
            windowMetrics = WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)
        }
        activity.addOnConfigurationChangedListener(listener)
        onDispose {
            activity.removeOnConfigurationChangedListener(listener)
        }
    }
    val windowDpSize = with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }
    return when {
        windowDpSize.width < 600.dp -> WindowSize.Compact
        windowDpSize.width < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}

@Composable
fun rememberWindowDpSize(): DpSize {
    val activity = LocalContext.current.findActivity() as ComponentActivity
    var windowMetrics by remember {
        mutableStateOf(
            WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)
        )
    }
    DisposableEffect(Unit) {
        val listener: (Configuration) -> Unit = {
            windowMetrics = WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity)
        }
        activity.addOnConfigurationChangedListener(listener)
        onDispose {
            activity.removeOnConfigurationChangedListener(listener)
        }
    }
    return with(LocalDensity.current) {
        windowMetrics.bounds.toComposeRect().size.toDpSize()
    }
}

enum class WindowSize { Compact, Medium, Expanded }