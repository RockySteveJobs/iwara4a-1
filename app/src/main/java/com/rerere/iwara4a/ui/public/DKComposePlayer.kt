package com.rerere.iwara4a.ui.public

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.rerere.iwara4a.ui.component.DefinitionControlView
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
import xyz.doikki.videoplayer.exo.ExoMediaPlayer
import xyz.doikki.videoplayer.player.VideoView

@Composable
fun DKComposePlayer(
    modifier: Modifier = Modifier,
    title: String,
    link: Map<String, String>
) {
    val context = LocalContext.current
    val direction = LocalScreenOrientation.current
    val videoView = remember {
        VideoView<ExoMediaPlayer>(context)
    }
    val controller = remember {
        StandardVideoController(context)
    }
    val definitionControlView = remember {
        DefinitionControlView(context)
    }

    BackHandler(
        enabled = direction == Configuration.ORIENTATION_LANDSCAPE
    ) {
        videoView.onBackPressed()
    }

    DisposableEffect(videoView) {
        onDispose {
            videoView.release()
        }
    }

    OnLifecycleEvent { owner, event ->
        if(owner is NavBackStackEntry) {
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    videoView.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    videoView.resume()
                }
                else -> {
                }
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            controller.setEnableOrientation(true)

            val completeView = CompleteView(it)
            val errorView = ErrorView(it)
            val prepareView = PrepareView(it)
            prepareView.setClickStart()
            val titleView = TitleView(it).apply {
                setTitle(title)
            }
            val mDefinitionControlView = definitionControlView.apply {
                setOnRateSwitchListener { url ->
                    videoView.setUrl(url)
                    videoView.replay(false)
                }
            }
            val gestureView = GestureView(it)

            controller.addControlComponent(
                completeView,
                errorView,
                prepareView,
                titleView,
                mDefinitionControlView,
                gestureView
            )

            videoView.setVideoController(controller)

            if (link.size > 1) {
                definitionControlView.setData(LinkedHashMap(link))
            }
            if (link.isNotEmpty()) {
                link.entries.first().value.let { vid ->
                    videoView.setUrl(vid)
                }
            }

            videoView.apply {
                start()
            }
        },
        update = {
            if (link.size > 1) {
                definitionControlView.setData(LinkedHashMap(link))
            }
            if (link.isNotEmpty()) {
                link.entries.first().value.let { vid ->
                    videoView.setUrl(vid)
                }
            }
        }
    )
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}