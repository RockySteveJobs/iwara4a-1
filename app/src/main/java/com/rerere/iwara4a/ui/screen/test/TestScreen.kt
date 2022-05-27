package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TestScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Green)
    )  {
        Box(
            modifier = Modifier
                .background(Color.Red)
                .fillMaxSize()
        )
    }
}