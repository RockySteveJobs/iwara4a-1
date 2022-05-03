package com.rerere.iwara4a.ui.component.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
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

    // Media state (video quality to media item)
    val mediaItems = mutableStateOf<Map<String, MediaItem>>(emptyMap())
    val currentQuality = mutableStateOf("Source")

    // player state
    val isPlaying = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    val videoSize = mutableStateOf(VideoSize.UNKNOWN)
    val playbackState = mutableStateOf(Player.STATE_IDLE)
    val videoDuration = mutableStateOf(0L)

    // controller state
    val showController = mutableStateOf(true)
    val showControllerTime = mutableStateOf(0L)
    val fullScreen = mutableStateOf(false)
    private var previousOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    fun handleMediaItem(
        items: Map<String, MediaItem>,
        autoPlay: Boolean,
        quality: String
    ) {
        if(items.isEmpty()) return

        println("handleMediaItem: ${items.size} | $quality | $autoPlay")

        currentQuality.value = quality
        mediaItems.value = items

        items[quality]?.let { player.setMediaItem(it) }

        if (autoPlay) {
            player.prepare()
        }
    }

    fun changeQuality(quality: String) {
        currentQuality.value = quality
        mediaItems.value[quality]?.let { player.setMediaItem(it) }
    }

    fun togglePlay() {
        if(playbackState.value == Player.STATE_IDLE) {
            player.prepare()
        }

        if (isPlaying.value) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun showController() {
        showController.value = true
        showControllerTime.value = System.currentTimeMillis()
    }

    fun toggleController() {
        if(!showController.value) {
            showControllerTime.value = System.currentTimeMillis()
            showController.value = true
        } else {
            showController.value = false
        }
    }

    fun toggleFullScreen(activity: Activity) {
        if (fullScreen.value) {
            exitFullScreen(activity)
        } else {
            enterFullScreen(activity)
        }
    }

    fun enterFullScreen(activity: Activity) {
        fullScreen.value = true
        previousOrientation = activity.requestedOrientation
        activity.requestedOrientation = if(videoSize.value.width > videoSize.value.height) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun exitFullScreen(activity: Activity) {
        WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
            show(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH
        }
        activity.requestedOrientation = previousOrientation
        fullScreen.value = false
    }

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

