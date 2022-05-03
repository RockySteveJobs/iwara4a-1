package com.rerere.iwara4a.ui.component.player

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.delay

@Composable
fun rememberPlayerState(
    context: Context = LocalContext.current,
    builder: (Context) -> ExoPlayer = {
        ExoPlayer.Builder(context).build()
    }
) = remember {
    PlayerState(
        context = context,
        builder = builder
    )
}

class PlayerState(
    context: Context,
    builder: (Context) -> ExoPlayer
) : Player.Listener {
    val player = builder(context).also {
        it.addListener(this)
    }

    // player state
    val isPlaying = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val videoSize = mutableStateOf(VideoSize.UNKNOWN)
    val playbackState = mutableStateOf(Player.STATE_IDLE)
    val videoDuration = mutableStateOf(0L)

    // controller state
    val showController = mutableStateOf(true)
    val fullScreen = mutableStateOf(false)

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        this.isPlaying.value = isPlaying
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        this.isLoading.value = isLoading
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        this.videoSize.value = videoSize
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if(playbackState == Player.STATE_READY) {
            this.videoDuration.value = player.duration
        }
        this.playbackState.value = playbackState
    }
}

@Composable
fun PlayerState.observeVideoPositionState() = produceState(
    initialValue = player.currentPosition
){
    while (true) {
        value = player.currentPosition
        delay(1000)
    }
}

