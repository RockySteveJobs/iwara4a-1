package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.rerere.iwara4a.model.detail.video.unescapeJava
import com.rerere.iwara4a.ui.component.player.PlayerController
import com.rerere.iwara4a.ui.component.player.VideoPlayer
import com.rerere.iwara4a.ui.component.player.adaptiveVideoSize
import com.rerere.iwara4a.ui.component.player.rememberPlayerState
import com.rerere.iwara4a.util.okhttp.UserAgentInterceptor
import okhttp3.OkHttpClient

fun String.toLink() = "https:" + unescapeJava(this).replace("\\/", "/")

@Composable
fun TestScreen() {
    Column {
        val state = rememberPlayerState { context ->
            ExoPlayer.Builder(
                context,
                DefaultMediaSourceFactory(
                    OkHttpDataSource.Factory(
                        OkHttpClient.Builder()
                            .addInterceptor(UserAgentInterceptor())
                            .build()
                    )
                )
            ).build().apply {
                addMediaItem(
                    MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")
                )
                prepare()
            }
        }
        VideoPlayer(
            modifier = Modifier.adaptiveVideoSize(state),
            state = state
        ){
            PlayerController(
                state = state,
                title = "测试"
            )
        }
    }
}