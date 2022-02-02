package com.rerere.iwara4a.ui.activity

import android.content.res.Configuration
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.R
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.screen.about.AboutScreen
import com.rerere.iwara4a.ui.screen.chat.ChatScreen
import com.rerere.iwara4a.ui.screen.dev.DevScreen
import com.rerere.iwara4a.ui.screen.download.DownloadScreen
import com.rerere.iwara4a.ui.screen.forum.ForumScreen
import com.rerere.iwara4a.ui.screen.friends.FriendsScreen
import com.rerere.iwara4a.ui.screen.history.HistoryScreen
import com.rerere.iwara4a.ui.screen.image.ImageScreen
import com.rerere.iwara4a.ui.screen.index.IndexScreen
import com.rerere.iwara4a.ui.screen.like.LikeScreen
import com.rerere.iwara4a.ui.screen.log.LoggerScreen
import com.rerere.iwara4a.ui.screen.login.LoginScreen
import com.rerere.iwara4a.ui.screen.message.MessageScreen
import com.rerere.iwara4a.ui.screen.playlist.PlaylistDialog
import com.rerere.iwara4a.ui.screen.search.SearchScreen
import com.rerere.iwara4a.ui.screen.self.SelfScreen
import com.rerere.iwara4a.ui.screen.setting.SettingScreen
import com.rerere.iwara4a.ui.screen.splash.SplashScreen
import com.rerere.iwara4a.ui.screen.user.UserScreen
import com.rerere.iwara4a.ui.screen.video.VideoScreen
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import com.rerere.iwara4a.util.okhttp.Retry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var okHttpClient: OkHttpClient
    private var screenOrientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 全屏
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // 初始化启动页面
        installSplashScreen()

        Log.i(TAG, "onCreate: Creating Activity")

        setContent {
            val navController = rememberAnimatedNavController()

            CompositionLocalProvider(
                LocalScreenOrientation provides screenOrientation,
                LocalNavController provides navController,
                LocalImageLoader provides ImageLoader(this)
                    .newBuilder()
                    .okHttpClient(
                        OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .addInterceptor(Retry())
                            .build()
                    )
                    .crossfade(true)
                    .error(R.drawable.failed)
                    .build()
            ) {
                ProvideWindowInsets {
                    Iwara4aTheme {
                        val systemUiController = rememberSystemUiController()
                        val dark = MaterialTheme.colors.isLight

                        // set ui color
                        SideEffect {
                            systemUiController.setNavigationBarColor(
                                Color.Transparent,
                                darkIcons = dark
                            )
                            systemUiController.setStatusBarColor(
                                Color.Transparent,
                                darkIcons = dark
                            )
                        }

                        AnimatedNavHost(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController,
                            startDestination = "splash",
                            enterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = {
                                        it
                                    },
                                    animationSpec = tween()
                                )
                            },
                            exitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = {
                                        -it
                                    },
                                    animationSpec = tween()
                                ) + fadeOut(
                                    animationSpec = tween()
                                )
                            },
                            popEnterTransition = {
                                slideInHorizontally(
                                    initialOffsetX = {
                                        -it
                                    },
                                    animationSpec = tween()
                                )
                            },
                            popExitTransition = {
                                slideOutHorizontally(
                                    targetOffsetX = {
                                        it
                                    },
                                    animationSpec = tween()
                                )
                            }
                        ) {
                            composable(
                                route = "splash",
                                exitTransition = {
                                    fadeOut()
                                }
                            ) {
                                SplashScreen(navController)
                            }

                            composable(
                                route = "index",
                                enterTransition = {
                                    fadeIn()
                                },
                                popEnterTransition = {
                                    slideInHorizontally(
                                        initialOffsetX = {
                                            -it
                                        },
                                        animationSpec = tween()
                                    )
                                }
                            ) {
                                IndexScreen(navController)
                            }

                            composable("login") {
                                LoginScreen(navController)
                            }

                            composable("video/{videoId}",
                                arguments = listOf(
                                    navArgument("videoId") {
                                        type = NavType.StringType
                                    }
                                ),
                                deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/videos/{videoId}"))
                            ) {
                                VideoScreen(
                                    navController,
                                    it.arguments?.getString("videoId")!!
                                )
                            }

                            composable("image/{imageId}",
                                arguments = listOf(
                                    navArgument("imageId") {
                                        type = NavType.StringType
                                    }
                                ),
                                deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/images/{imageId}"))) {
                                ImageScreen(
                                    navController,
                                    it.arguments?.getString("imageId")!!
                                )
                            }

                            composable("user/{userId}",
                                arguments = listOf(
                                    navArgument("userId") {
                                        type = NavType.StringType
                                    }
                                ),
                                deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/users/{userId}"))) {
                                UserScreen(
                                    navController,
                                    it.arguments?.getString("userId")!!
                                )
                            }

                            composable(
                                route = "search"
                            ) {
                                SearchScreen()
                            }

                            composable("about") {
                                AboutScreen()
                            }


                            dialog("playlist?nid={nid}", arguments = listOf(
                                navArgument("nid") {
                                    defaultValue = 0
                                    type = NavType.IntType
                                }
                            )) {
                                PlaylistDialog(
                                    navController,
                                    it.arguments!!.getInt("nid"),
                                    it.arguments!!.getString("playlist-id") ?: ""
                                )
                            }

                            composable("playlist?playlist-id={playlist-id}", arguments = listOf(
                                navArgument("playlist-id") {
                                    defaultValue = ""
                                    type = NavType.StringType
                                }
                            )) {
                                PlaylistDialog(
                                    navController,
                                    it.arguments!!.getInt("nid"),
                                    it.arguments!!.getString("playlist-id") ?: ""
                                )
                            }

                            composable("like") {
                                LikeScreen(navController)
                            }

                            composable("download") {
                                DownloadScreen(navController)
                            }

                            composable("setting") {
                                SettingScreen(navController)
                            }

                            composable("history") {
                                HistoryScreen()
                            }

                            composable("logger") {
                                LoggerScreen()
                            }

                            composable("self") {
                                SelfScreen()
                            }

                            composable("forum") {
                                ForumScreen()
                            }

                            composable("chat") {
                                ChatScreen()
                            }

                            composable("friends") {
                                FriendsScreen()
                            }


                            composable("message") {
                                MessageScreen()
                            }

                            composable("dev") {
                                Box(modifier = Modifier.drawWithContent {
                                    drawContent()
                                    drawContext.canvas.nativeCanvas.drawText(
                                        "这个页面用于测试一些组件, 没啥好玩的",
                                        0f,
                                        20.dp.toPx(),
                                        Paint().apply {
                                            color = android.graphics.Color.RED
                                            this.textSize = 15.sp.toPx()
                                        }
                                    )
                                }) {
                                    DevScreen()
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 禁止强制暗色模式，因为已经适配了夜间模式，所以不需要强制反色
            // 国产UI似乎必需这样做(isForceDarkAllowed = false)才能阻止反色，原生会自动识别
            val existingComposeView = window.decorView
                .findViewById<ViewGroup>(android.R.id.content)
                .getChildAt(0) as? ComposeView
            existingComposeView?.isForceDarkAllowed = false
        }

        // 是否允许屏幕捕捉
        lifecycleScope.launch {
            getSharedPreferences(packageName +"_preferences", MODE_PRIVATE).getBoolean("setting.preventscreencaptcha", false).let {
                if(it){
                    Toast.makeText(this@MainActivity, "已开启隐私模式", Toast.LENGTH_SHORT).show()
                    window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (screenOrientation != newConfig.orientation) {
            screenOrientation = newConfig.orientation
            Log.i(TAG, "onConfigurationChanged: CONFIG CHANGE: ${newConfig.orientation}")
        }
    }
}