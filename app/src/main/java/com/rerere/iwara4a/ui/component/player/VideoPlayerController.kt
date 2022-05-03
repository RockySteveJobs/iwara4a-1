package com.rerere.iwara4a.ui.component.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.video.VideoSize
import com.rerere.iwara4a.ui.component.basic.Centered
import com.rerere.iwara4a.util.prettyDuration
import kotlin.math.roundToLong

fun Modifier.adaptiveVideoSize(state: PlayerState) = this
    .fillMaxWidth()
    .then(
        if(state.fullScreen.value) {
            Modifier.fillMaxHeight()
        } else {
            Modifier.aspectRatio(16/9f)
        }
    )

@Composable
fun VideoPlayerWithController(
    modifier: Modifier,
    state: PlayerState,
    title: String
) {
    val scope = rememberCoroutineScope()
    Box(modifier) {
        VideoPlayer(
            modifier = Modifier.fillMaxSize(),
            state = state
        )
        AnimatedVisibility(state.showController.value) {
            Controller(
                title = title,
                state = state
            )
        }
    }
}

@Composable
private fun Controller(
    title: String,
    state: PlayerState
) {
    Column(Modifier.fillMaxSize()) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

        Centered(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (state.isLoading.value && !state.isPlaying.value) {
                CircularProgressIndicator(
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }


        // Bottom Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (state.isPlaying.value) {
                        state.player.pause()
                    } else {
                        state.player.play()
                    }
                }
            ) {
                if (state.isPlaying.value) {
                    Icon(Icons.Outlined.Pause, null, tint = Color.White)
                } else {
                    Icon(Icons.Outlined.PlayArrow, null, tint = Color.White)
                }
            }

            val position = state.observeVideoPositionState()
            val progress = remember(position.value, state.videoDuration.value) {
                if (state.videoDuration.value > 0) {
                    position.value.toFloat() / state.videoDuration.value.toFloat()
                } else {
                    0f
                }
            }
            var progressSlide by remember(progress) {
                mutableStateOf(progress)
            }
            Slider(
                modifier = Modifier.weight(1f),
                value = progressSlide,
                onValueChange = { progressSlide = it },
                onValueChangeFinished = {
                    state.player.seekTo(
                        (state.videoDuration.value * progressSlide).roundToLong()
                    )
                }
            )

            Text(
                text = prettyDuration((state.videoDuration.value * progressSlide).roundToLong()) + " / " + prettyDuration(
                    state.videoDuration.value
                ),
                color = Color.White
            )

            IconButton(
                onClick = {
                    if(state.videoSize.value != VideoSize.UNKNOWN) {
                        state.fullScreen.value = !state.fullScreen.value
                    }
                }
            ) {
                Icon(
                    imageVector = if(state.fullScreen.value) Icons.Outlined.FullscreenExit else Icons.Outlined.Fullscreen,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}