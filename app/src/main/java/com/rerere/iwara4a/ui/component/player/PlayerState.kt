package com.rerere.iwara4a.ui.component.player

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer

@Composable
fun rememberPlayerState(
    context: Context = LocalContext.current
) = remember {
    PlayerState(
        context = context
    )
}

class PlayerState(
    context: Context
) {
    val player = ExoPlayer.Builder(context).build()

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }
}

