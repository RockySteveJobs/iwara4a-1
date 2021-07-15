package com.rerere.iwara4a.ui.activity

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.screen.about.AboutScreen
import com.rerere.iwara4a.ui.screen.image.ImageScreen
import com.rerere.iwara4a.ui.screen.index.IndexScreen
import com.rerere.iwara4a.ui.screen.like.LikeScreen
import com.rerere.iwara4a.ui.screen.login.LoginScreen
import com.rerere.iwara4a.ui.screen.playlist.PlaylistDialog
import com.rerere.iwara4a.ui.screen.search.SearchScreen
import com.rerere.iwara4a.ui.screen.splash.SplashScreen
import com.rerere.iwara4a.ui.screen.user.UserScreen
import com.rerere.iwara4a.ui.screen.video.VideoScreen
import com.rerere.iwara4a.ui.theme.Iwara4aTheme
import com.rerere.iwara4a.ui.theme.uiBackGroundColor
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var okHttpClient: OkHttpClient
    var screenOrientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)

    @RequiresApi(Build.VERSION_CODES.R)
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContent {
            CompositionLocalProvider(
                LocalScreenOrientation provides screenOrientation
            ) {
                ProvideWindowInsets {
                    Iwara4aTheme {
                        val navController = rememberNavController()

                        val systemUiController = rememberSystemUiController()
                        val primaryColor = MaterialTheme.colors.uiBackGroundColor
                        val dark = MaterialTheme.colors.isLight

                        // set ui color
                        SideEffect {
                            systemUiController.setNavigationBarColor(primaryColor, darkIcons = dark)
                            systemUiController.setStatusBarColor(Color.Transparent, darkIcons = dark)
                        }

                        NavHost(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController,
                            startDestination = "splash"
                        ) {
                            composable("splash") {
                                SplashScreen(navController)
                            }

                            composable("index") {
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
                                deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/videos/{videoId}"))) {
                                VideoScreen(navController, it.arguments?.getString("videoId")!!)

                            }

                            composable("image/{imageId}",
                                arguments = listOf(
                                    navArgument("imageId") {
                                        type = NavType.StringType
                                    }
                                ),
                                deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/images/{imageId}"))) {
                                ImageScreen(navController, it.arguments?.getString("imageId")!!)
                            }

                            composable("user/{userId}",
                                arguments = listOf(
                                    navArgument("userId") {
                                        type = NavType.StringType
                                    }
                                ),
                                deepLinks = listOf(NavDeepLink("https://ecchi.iwara.tv/users/{userId}"))) {
                                UserScreen(navController, it.arguments?.getString("userId")!!)
                            }

                            composable("search") {
                                SearchScreen(navController)
                            }

                            composable("about"){
                                AboutScreen(navController)
                            }

                            dialog("playlist"){
                                Dialog(onDismissRequest = {
                                    navController.popBackStack()
                                }) {
                                    PlaylistDialog(navController)
                                }
                            }

                            composable("like"){
                                LikeScreen(navController)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        screenOrientation = newConfig.orientation
    }
}