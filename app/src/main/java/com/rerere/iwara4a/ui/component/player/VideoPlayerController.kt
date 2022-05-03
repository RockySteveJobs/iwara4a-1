package com.rerere.iwara4a.ui.component.player

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.video.VideoSize
import com.rerere.iwara4a.ui.component.basic.Centered
import com.rerere.iwara4a.util.prettyDuration
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

fun Modifier.adaptiveVideoSize(state: PlayerState) = fillMaxWidth()
    .then(
        if (state.fullScreen.value) {
            Modifier.fillMaxHeight()
        } else {
            Modifier.aspectRatio(16 / 9f)
        }
    )

@Composable
fun PlayerController(
    state: PlayerState,
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    onChangeVideoQuality: (String) -> Unit = {},
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        state.showController()
        while (true) {
            if (System.currentTimeMillis() - state.showControllerTime.value > 4000) {
                state.showController.value = false
            }
            delay(1000)
        }
    }

    // 返回退出全屏
    BackHandler(
        enabled = state.fullScreen.value
    ) {
        state.exitFullScreen(context as Activity)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        state.togglePlay()
                    },
                    onTap = {
                        state.toggleController()
                    }
                )
            }
    ) {
        AnimatedVisibility(
            visible = state.showController.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Controller(
                title = title,
                state = state,
                navigationIcon = navigationIcon,
                onQualityChange = onChangeVideoQuality
            )
        }
    }
}

@Composable
private fun Controller(
    title: String,
    navigationIcon: @Composable () -> Unit,
    state: PlayerState,
    onQualityChange: (String) -> Unit
) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize()) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            navigationIcon()

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            var expand by remember { mutableStateOf(false) }
            TextButton(
                onClick = {  expand = !expand },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = state.currentQuality.value
                )
                DropdownMenu(
                    expanded = expand,
                    onDismissRequest = { expand = false }
                ) {
                    state.mediaItems.value.forEach { entry ->
                        DropdownMenuItem(
                            text = {
                                Text(entry.key)
                            },
                            onClick = {
                                state.changeQuality(entry.key)
                                onQualityChange(entry.key)
                                expand = false
                            }
                        )
                    }
                }
            }
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
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    state.togglePlay()
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
                    if (state.videoSize.value != VideoSize.UNKNOWN) {
                        if (state.fullScreen.value) {
                            state.exitFullScreen(context as Activity)
                        } else {
                            state.enterFullScreen(context as Activity)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (state.fullScreen.value) Icons.Outlined.FullscreenExit else Icons.Outlined.Fullscreen,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}