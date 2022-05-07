package com.rerere.iwara4a.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.local.LocalPIPMode
import com.rerere.iwara4a.ui.local.LocalSelfData
import com.rerere.iwara4a.ui.screen.about.AboutScreen
import com.rerere.iwara4a.ui.screen.download.DownloadScreen
import com.rerere.iwara4a.ui.screen.follow.FollowScreen
import com.rerere.iwara4a.ui.screen.forum.ForumScreen
import com.rerere.iwara4a.ui.screen.friends.FriendsScreen
import com.rerere.iwara4a.ui.screen.history.HistoryScreen
import com.rerere.iwara4a.ui.screen.image.ImageScreen
import com.rerere.iwara4a.ui.screen.index.IndexScreen
import com.rerere.iwara4a.ui.screen.like.LikeScreen
import com.rerere.iwara4a.ui.screen.log.LogScreen
import com.rerere.iwara4a.ui.screen.login.LoginScreen
import com.rerere.iwara4a.ui.screen.message.MessageScreen
import com.rerere.iwara4a.ui.screen.playlist.PlaylistDialog
import com.rerere.iwara4a.ui.screen.search.SearchScreen
import com.rerere.iwara4a.ui.screen.self.SelfScreen
import com.rerere.iwara4a.ui.screen.setting.SettingScreen
import com.rerere.iwara4a.ui.screen.test.TestScreen
import com.rerere.iwara4a.ui.screen.user.UserScreen
import com.rerere.iwara4a.ui.screen.video.VideoScreen
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import dagger.hilt.android.AndroidEntryPoint
import me.rerere.compose_setting.preference.mmkvPreference
import soup.compose.material.motion.materialSharedAxisZIn
import soup.compose.material.motion.materialSharedAxisZOut

@AndroidEntryPoint
class RouterActivity : AppCompatActivity() {
    val viewModel: RouterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 全屏
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Night Mode
        mmkvPreference.getInt("nightMode", 0).let {
            when (it) {
                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> error("bad const of night mode")
            }
        }

        // 初始化启动页面
        installSplashScreen().setKeepOnScreenCondition {
            !viewModel.userDataFetched
        }

        setContent {
            val navController = rememberAnimatedNavController()
            val density = LocalDensity.current

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalSelfData provides viewModel.userData,
                LocalPIPMode provides viewModel.pipMode
            ) {
                Iwara4aTheme {
                    AnimatedNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colors.background),
                        navController = navController,
                        startDestination = "index",
                         enterTransition = {
                            materialSharedAxisZIn().transition(true, density)
                        },
                        exitTransition = {
                            materialSharedAxisZOut().transition(true, density)
                        },
                        popEnterTransition = {
                            materialSharedAxisZIn().transition(false, density)
                        },
                        popExitTransition = {
                            materialSharedAxisZOut().transition(false, density)
                        }
                    ) {
                        composable("index") {
                            LaunchedEffect(viewModel.userData, viewModel.userDataFetched) {
                                if (viewModel.userDataFetched && viewModel.userData == Self.GUEST) {
                                    navController.navigate("login") {
                                        popUpTo("index") {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
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
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "https://ecchi.iwara.tv/videos/{videoId}"
                                },
                                navDeepLink {
                                    uriPattern = "iwara4a://video/{videoId}"
                                }
                            )
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
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "https://ecchi.iwara.tv/images/{imageId}"
                                }
                            )
                        ) {
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
                            deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/users/{userId}"))
                        ) {
                            UserScreen(
                                navController,
                                it.arguments?.getString("userId")!!
                            )
                        }

                        composable(
                            route = "search",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "iwara4a://search"
                                }
                            )
                        ) {
                            SearchScreen()
                        }

                        composable("about") {
                            AboutScreen()
                        }


                        dialog(
                            route = "playlist?nid={nid}",
                            arguments = listOf(
                                navArgument("nid") {
                                    defaultValue = 0
                                    type = NavType.IntType
                                }
                            ),
                            dialogProperties = DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        ) {
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

                        composable(
                            route = "download",
                            deepLinks = listOf(
                                navDeepLink {
                                    uriPattern = "iwara4a://download"
                                }
                            )
                        ) {
                            DownloadScreen(navController)
                        }

                        composable("setting") {
                            SettingScreen()
                        }

                        composable("history") {
                            HistoryScreen()
                        }

                        composable("log") {
                            LogScreen()
                        }

                        composable("self") {
                            SelfScreen()
                        }

                        composable("forum") {
                            ForumScreen()
                        }

                        composable("friends") {
                            FriendsScreen()
                        }

                        composable("message") {
                            MessageScreen()
                        }

                        composable("following") {
                            FollowScreen()
                        }

                        composable("test") {
                            TestScreen()
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
        if (mmkvPreference.getBoolean("setting.preventscreencaptcha", false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.pipMode = isInPictureInPictureMode
    }
}