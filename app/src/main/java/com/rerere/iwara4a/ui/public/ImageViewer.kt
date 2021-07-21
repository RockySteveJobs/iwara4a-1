package com.rerere.iwara4a.ui.public

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import coil.imageLoader
import coil.request.ImageRequest
import com.ortiz.touchview.TouchImageView

@Composable
fun ImageViewer(modifier: Modifier, link: String) {
    val context = LocalContext.current
    AndroidView(modifier = modifier, factory = {
        TouchImageView(it).apply {
            val request = ImageRequest.Builder(context)
                .data(link)
                .target { drawable ->
                    this.setImageDrawable(drawable)
                }
                .build()
            context.imageLoader.enqueue(request)
        }
    }) {
        val request = ImageRequest.Builder(context)
            .data(link)
            .target { drawable ->
                it.setImageDrawable(drawable)
            }
            .build()
        context.imageLoader.enqueue(request)
    }
}