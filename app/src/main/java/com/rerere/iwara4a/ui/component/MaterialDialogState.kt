package com.rerere.iwara4a.ui.component

import androidx.compose.runtime.*

@Stable
class MaterialDialogState {
    private var show by mutableStateOf(false)

    fun show() {
        show = true
    }

    fun hide() {
        show = false
    }

    fun isVisible() = show
}

@Composable
fun rememberMaterialDialogState() = remember {
    MaterialDialogState()
}