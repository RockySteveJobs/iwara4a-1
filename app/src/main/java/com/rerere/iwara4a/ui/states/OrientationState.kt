package com.rerere.iwara4a.ui.states

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun rememberOrientationState(): State<Int> {
    val configuration = LocalConfiguration.current
    val orientation = remember { mutableStateOf(configuration.orientation) }
    LaunchedEffect(configuration) {
        if(configuration.orientation != orientation.value) {
            orientation.value = configuration.orientation
        }
    }
    return orientation
}