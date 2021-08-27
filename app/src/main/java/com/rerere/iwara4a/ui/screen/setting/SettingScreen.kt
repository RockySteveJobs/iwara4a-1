package com.rerere.iwara4a.ui.screen.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.alorma.settings.composables.SettingsGroup
import com.alorma.settings.composables.SettingsMenuLink
import com.alorma.settings.composables.SettingsSwitch
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.ui.public.rememberBooleanPreference

@Composable
fun SettingScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            DefTopBar(navController, "设置")
        }
    ) {
        Box(modifier = Modifier.navigationBarsPadding()) {
            Body(navController)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Body(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsGroup(title = {
            Text(text = "界面设置")
        }) {
            var followSystemDarkMode by rememberBooleanPreference(
                keyName = "setting.followSystemDarkMode",
                initialValue = true,
                defaultValue = true
            )
            SettingsSwitch(
                icon = {
                    Icon(Icons.Default.DarkMode, null)
                },
                title = {
                    Text(text = "跟随系统夜间模式")
                },
                subtitle = {
                    Text(text = "自动跟随系统夜间模式")
                },
                checked = followSystemDarkMode,
                onCheckedChange = {
                    followSystemDarkMode = it
                }
            )
            var darkMode by rememberBooleanPreference(
                keyName = "setting.darkMode",
                initialValue = false,
                defaultValue = false
            )
            AnimatedVisibility(visible = !followSystemDarkMode) {
                SettingsSwitch(
                    icon = {
                        Icon(Icons.Default.DarkMode, null)
                    },
                    title = {
                        Text(text = "暗色模式")
                    },
                    subtitle = {
                        Text(text = "是否启用暗色模式")
                    },
                    checked = darkMode,
                    onCheckedChange = {
                        darkMode = it
                    }
                )
            }
        }

        SettingsGroup(
            title = {
                Text(text = "视频设置")
            }
        ) {
            var autoPlayVideo by rememberBooleanPreference(
                keyName = "setting.autoPlayVideo",
                initialValue = true,
                defaultValue = true
            )
            SettingsSwitch(
                icon = {
                    Icon(Icons.Default.PlayArrow, null)
                },
                title = {
                    Text(text = "自动播放视频")
                },
                subtitle = {
                    Text(text = "当打开一个视频页面后会自动加载视频并播放")
                },
                checked = autoPlayVideo,
                onCheckedChange = {
                    autoPlayVideo = it
                }
            )
            AnimatedVisibility(visible = autoPlayVideo) {
                var autoPlayOnWifi by rememberBooleanPreference(
                    keyName = "setting.autoPlayVideoOnWifi",
                    initialValue = false
                )
                SettingsSwitch(
                    icon = {
                        Icon(Icons.Default.Wifi, null)
                    },
                    title = {
                        Text(text = "仅在WIFI下自动播放视频")
                    },
                    subtitle = {
                        Text(text = "顾名思义...")
                    },
                    checked = autoPlayOnWifi,
                    onCheckedChange = {
                        autoPlayOnWifi = it
                    }
                )
            }
        }

        SettingsGroup(
            title = {
                Text(text = "APP信息")
            }
        ) {
            SettingsMenuLink(
                title = {
                    Text(text = "关于")
                },
                icon = {
                    Icon(Icons.Default.Copyright, null)
                }
            ) {
                navController.navigate("about")
            }

            SettingsMenuLink(
                title = {
                    Text(text = "日志信息")
                },
                icon = {
                    Icon(Icons.Default.Book, null)
                }
            ) {
                navController.navigate("logger")
            }
        }
    }
}