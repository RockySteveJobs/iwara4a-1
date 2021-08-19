package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import coil.compose.rememberImagePainter
import kotlin.math.roundToInt

@Composable
fun ImagePreview(link: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        var zoom by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Image(
            painter = rememberImagePainter(data = link),
            contentDescription = "image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom
                )
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, gestureZoom, _ ->
                            zoom *= gestureZoom
                            val x = pan.x * zoom
                            val y = pan.y * zoom
                            offsetX += (x  - y)
                            offsetY += (x + y)
                        }
                    )
                }
                .fillMaxSize()
        )
    }
}