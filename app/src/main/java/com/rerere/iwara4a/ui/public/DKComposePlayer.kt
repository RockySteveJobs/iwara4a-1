package com.rerere.iwara4a.ui.public

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.rerere.iwara4a.ui.component.DefinitionControlView
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.theme.uiBackGroundColor
import com.rerere.iwara4a.util.autoRotation
import com.rerere.iwara4a.util.isFreeNetwork
import com.rerere.iwara4a.util.okhttp.SmartDns
import okhttp3.OkHttpClient
import xyz.doikki.videocontroller.StandardVideoController
import xyz.doikki.videocontroller.component.*
import xyz.doikki.videoplayer.exo.ExoMediaPlayer
import xyz.doikki.videoplayer.exo.ExoMediaSourceHelper
import xyz.doikki.videoplayer.player.VideoView

private const val TAG = "DKComposePlayer"

@Composable
fun DKComposePlayer(
    modifier: Modifier = Modifier,
    title: String,
    link: Map<String, String>
) {
    val autoPlayVideo by rememberBooleanPreference(
        keyName = "setting.autoPlayVideo",
        initialValue = true
    )
    val autoPlayOnWifi by rememberBooleanPreference(
        keyName = "setting.autoPlayVideoOnWifi",
        initialValue = false
    )

    val context = LocalContext.current
    val direction = LocalScreenOrientation.current
    var playerState by remember {
        mutableStateOf(10)
    }
    val videoView = remember {
        VideoView<ExoMediaPlayer>(context).apply {
            setOnStateChangeListener(object : VideoView.SimpleOnStateChangeListener() {
                override fun onPlayerStateChanged(state: Int) {
                    playerState = state
                }
            })

            val source = OkHttpDataSource.Factory(
                OkHttpClient.Builder().dns(SmartDns).build()
            )
            val instance = ExoMediaSourceHelper.getInstance(context)
            instance.javaClass.apply {
                getDeclaredField("mHttpDataSourceFactory").apply {
                    isAccessible = true
                    if(type != source.javaClass) {
                        set(instance, source)
                        println("已替换HTTP客户端")
                    }
                }
            }
        }
    }
    val controller = remember {
        StandardVideoController(context)
    }
    val definitionControlView = remember {
        DefinitionControlView(context)
    }

    val systemUiController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colors.uiBackGroundColor
    val dark = MaterialTheme.colors.isLight
    SideEffect {
        systemUiController.setNavigationBarColor(
            primaryColor,
            darkIcons = dark
        )
        systemUiController.setStatusBarColor(
            Color.Transparent,
            darkIcons = dark
        )
    }

    BackHandler(
        enabled = playerState == 11
    ) {
        videoView.onBackPressed()
    }

    DisposableEffect(videoView) {
        onDispose {
            videoView.release()
        }
    }

    OnLifecycleEvent { owner, event ->
        if (owner is NavBackStackEntry) {
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
            controller.setEnableOrientation(context.autoRotation)

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

            videoView
        },
        update = {
            link.forEach {
                Log.i(TAG, "DKComposePlayer: Link[${it.key}] ${it.value}")
            }
            if (link.size > 1) {
                definitionControlView.setData(LinkedHashMap(link))
            }
            if (link.isNotEmpty()) {
                link.entries.first().value.let { vid ->
                    videoView.setUrl(vid)

                    if (autoPlayVideo) {
                        if (autoPlayOnWifi) {
                            if (context.isFreeNetwork()) {
                                videoView.start()
                            }
                        } else {
                            videoView.start()
                        }
                    }
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