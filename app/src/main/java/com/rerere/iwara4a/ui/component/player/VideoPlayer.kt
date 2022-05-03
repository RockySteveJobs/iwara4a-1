package com.rerere.iwara4a.ui.component.player

import android.view.SurfaceView
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.rerere.iwara4a.ui.component.OnLifecycleEvent

@Composable
private fun VideoPlayerSurface(
    modifier: Modifier = Modifier,
    state: PlayerState
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
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

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    state: PlayerState,
) {
    VideoPlayerSurface(
        modifier = modifier,
        state = state
    )

    OnLifecycleEvent { _, event ->
        when(event) {
            Lifecycle.Event.ON_PAUSE -> {
                state.player.pause()
            }
            Lifecycle.Event.ON_RESUME -> {
                state.player.play()
            }
            else -> {}
        }
    }

    DisposableEffect(state) {
        onDispose {
            state.player.release()
        }
    }
}