package com.rerere.iwara4a.ui.public

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.video.VideoSize

private const val TAG = "ExoPlayerCompose"

@Composable
fun ExoPlayer(modifier: Modifier = Modifier, exoPlayer: SimpleExoPlayer, videoLink: String) {
    val autoPlayVideo by rememberBooleanPreference(keyName = "setting.autoPlayVideo", initialValue = true, defaultValue = true)
    LaunchedEffect(videoLink) {
        if (videoLink.isNotEmpty()) {
            Log.i(TAG, "ExoPlayer: Loading Video: $videoLink")
            exoPlayer.setMediaItem(MediaItem.fromUri(videoLink))
            if(autoPlayVideo) {
                exoPlayer.prepare()
            }
        }
    }

    Box(contentAlignment = Alignment.TopStart) {
        AndroidView(modifier = modifier, factory = {
            PlayerView(it).apply {
                player = exoPlayer
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            Log.i(TAG, "ExoPlayer: Released the Player")
        }
    }
}

fun VideoSize.isVertVideo() = height > width

val ExoPlayer.isReady
    get() = videoSize.width > 0