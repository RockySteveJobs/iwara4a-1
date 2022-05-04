package com.rerere.iwara4a.ui.component.player

import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.google.android.exoplayer2.video.VideoSize
import com.rerere.iwara4a.ui.component.basic.Centered
import com.rerere.iwara4a.ui.states.OnLifecycleEvent

@Composable
private fun VideoPlayerSurface(
    state: PlayerState
) {
    val context = LocalContext.current
    Box {
        AndroidView(
            modifier = Modifier.aspectRatio(
                ratio = if (state.videoSize.value != VideoSize.UNKNOWN) {
                    state.videoSize.value.width / state.videoSize.value.height.toFloat()
                } else {
                    16 / 9f
                },
                matchHeightConstraintsFirst = true
            ),
            factory = {
                SurfaceView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }.also {
                    state.player.setVideoSurfaceView(it)
                }
            }
        )
    }
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    state: PlayerState,
    controller: @Composable () -> Unit = {}
) {
    CompositionLocalProvider(LocalContentColor provides Color.White) {
        Centered(
            modifier = Modifier
                .background(Color.Black)
                .then(modifier)
        ) {
            VideoPlayerSurface(
                state = state
            )
            controller()
        }

        OnLifecycleEvent { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    state.player.pause()
                }
                Lifecycle.Event.ON_START -> {
                    state.player.play()
                }
                else -> {}
            }
        }
    }
}