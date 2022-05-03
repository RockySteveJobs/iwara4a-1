package com.rerere.iwara4a.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private const val TAG = "DKComposePlayer"

@Composable
fun DKComposePlayer(
    modifier: Modifier = Modifier,
    title: String,
    link: Map<String, String>
) {
    /*val autoPlayVideo by rememberBooleanPreference(
        key = "setting.autoPlayVideo",
        default = true
    )
    val autoPlayOnWifi by rememberBooleanPreference(
        key = "setting.autoPlayVideoOnWifi",
        default = false
    )
    var videoQuality by rememberStringPreference(
        key = "setting.videoQuality",
        default = "Source"
    )

    val context = LocalContext.current
    // val direction = LocalScreenOrientation.current
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
        }
    }
    val controller = remember {
        StandardVideoController(context)
    }
    val definitionControlView = remember {
        DefinitionControlView(context)
    }

    ApplyBarColor()

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
                setOnRateSwitchListener { name, url ->
                    videoView.setUrl(url)
                    videoView.replay(false)
                    videoQuality = name
                    Log.i(TAG, "DKComposePlayer: Video Quality => $name")
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

            videoView
        },
        update = {
            link.forEach {
                Log.i(TAG, "DKComposePlayer: Link[${it.key}] ${it.value}")
            }
            if (link.size > 1) {
                val map = LinkedHashMap(link)
                definitionControlView.setData(map)
                val index = if(map.containsKey(videoQuality)){
                    map.entries.reversed().indexOfFirst {
                        it.key.equals(videoQuality, true)
                    }
                } else 0
                Log.i(TAG, "DKComposePlayer: Index: $index")
                definitionControlView.updateCurrentVideoQualityText(index)
                videoView.setUrl(map.entries.reversed()[index].value)
                if (autoPlayVideo) {
                    if (autoPlayOnWifi) {
                        if (context.isFreeNetwork()) {
                            videoView.start()
                        }
                    } else {
                        videoView.start()
                    }
                }
            } else {
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
        }
    )*/
}

