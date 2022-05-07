package com.rerere.iwara4a.ui.component.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.rerere.iwara4a.ui.states.OnLifecycleEvent
import java.lang.ref.WeakReference

@Composable
private fun VideoPlayerSurface(
    state: PlayerState
) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }.also {
                state.surfaceView = WeakReference(it)
                it.player = state.player
                it.useController = false
                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    DisposableEffect(state) {
        onDispose {
            state.player.release()
        }
    }
}

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    state: PlayerState,
    controller: @Composable () -> Unit = {}
) {
    CompositionLocalProvider(
        LocalContentColor provides Color.White
    ) {
        Box(
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