package com.rerere.iwara4a.ui.public

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlin.math.roundToInt

@OptIn(ExperimentalCoilApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ImagePreview(link: String) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        var zoom by remember { mutableStateOf(1f) }
        val animatedZoom by animateFloatAsState(targetValue = zoom)
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Image(
            painter = rememberImagePainter(data = link),
            contentDescription = "image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .graphicsLayer(
                    scaleX = animatedZoom,
                    scaleY = animatedZoom
                )
                .combinedClickable(
                    onDoubleClick = {
                        zoom = if (zoom == 1f) {
                            3f
                        } else {
                            1f
                        }
                    },
                    onClick = {}
                )
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { _, pan, gestureZoom, _ ->
                            zoom *= gestureZoom
                            val x = pan.x * zoom
                            val y = pan.y * zoom
                            offsetX += x
                            offsetY += y
                        }
                    )
                }
                .fillMaxSize()
        )
    }
}