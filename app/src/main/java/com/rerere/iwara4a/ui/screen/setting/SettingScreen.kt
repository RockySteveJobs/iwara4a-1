package com.rerere.iwara4a.ui.screen.setting

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.AppBarStyle
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.local.LocalNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.rerere.compose_setting.components.SettingItemCategory
import me.rerere.compose_setting.components.types.SettingBooleanItem
import me.rerere.compose_setting.components.types.SettingLinkItem
import me.rerere.compose_setting.preference.rememberBooleanPreference
import me.rerere.compose_setting.preference.rememberIntPreference
import me.rerere.md3compat.ThemeChooser
import java.util.*

@Composable
fun SettingScreen() {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = rememberSplineBasedDecay()
    )
    Scaffold(
        topBar = {
            Md3TopBar(
                navigationIcon = {
                    BackIcon()
                },
                title = {
                    Text(stringResource(id = R.string.screen_setting_topbar_title))
                },
                appBarStyle = AppBarStyle.Large,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Body(scrollBehavior, it)
    }
}

@Composable
private fun Body(scrollBehavior: TopAppBarScrollBehavior, paddingValues: PaddingValues) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(paddingValues)
    ) {
        SettingItemCategory(
            title = {
                Text(
                    text = stringResource(id = R.string.screen_setting_personalize_title)
                )
            }
        ) {
            var nightMode by rememberIntPreference(key = "nightMode", default = 0)
            var nightModeMenu by remember {
                mutableStateOf(false)
            }
            SettingLinkItem(
                title = {
                    Text("暗色模式")
                    DropdownMenu(
                        expanded = nightModeMenu,
                        onDismissRequest = { nightModeMenu = false }
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Outlined.Android, null)
                            },
                            trailingIcon = {
                                if (nightMode == 0) {
                                    Icon(Icons.Outlined.Check, null)
                                }
                            },
                            text = {
                                Text("跟随系统")
                            },
                            onClick = {
                                nightModeMenu = false
                                nightMode = 0
                                scope.launch {
                                    delay(150L)
                                    (context as Activity).recreate()
                                }
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Outlined.LightMode, null)
                            },
                            trailingIcon = {
                                if (nightMode == 1) {
                                    Icon(Icons.Outlined.Check, null)
                                }
                            },
                            text = {
                                Text("亮色")
                            },
                            onClick = {
                                nightModeMenu = false
                                nightMode = 1
                                scope.launch {
                                    delay(150L)
                                    (context as Activity).recreate()
                                }
                            }
                        )
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Outlined.DarkMode, null)
                            },
                            trailingIcon = {
                                if (nightMode == 2) {
                                    Icon(Icons.Outlined.Check, null)
                                }
                            },
                            text = {
                                Text("暗色")
                            },
                            onClick = {
                                nightModeMenu = false
                                nightMode = 2
                                scope.launch {
                                    delay(150L)
                                    (context as Activity).recreate()
                                }
                            }
                        )
                    }
                },
                icon = {
                    Icon(Icons.Outlined.DarkMode, null)
                }
            ) {
                nightModeMenu = true
            }
            // 主题
            var expandTheme by remember {
                mutableStateOf(false)
            }
            SettingLinkItem(
                title = {
                    Text(stringResource(R.string.screen_setting_personalize_theme_mode))
                },
                text = {
                    Text(stringResource(R.string.screen_setting_personalize_theme_mode_subtitle))
                },
                onClick = {
                    expandTheme = !expandTheme
                },
                icon = {
                    Icon(Icons.Outlined.Palette, null)
                }
            )
            AnimatedVisibility(expandTheme) {
                ThemeChooser()
            }

            // 阻止多任务预览
            SettingBooleanItem(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_scraping_title))
                },
                icon = {
                    Icon(Icons.Outlined.ScreenShare, null)
                },
                text = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_preventscreen_subtitle))
                },
                state = rememberBooleanPreference(
                    key = "setting.preventscreencaptcha",
                    default = false
                )
            )

            // 演示模式
            if (Locale.getDefault().language == Locale.CHINA.language) {
                SettingBooleanItem(
                    title = {
                        Text("演示模式")
                    },
                    icon = {
                        Icon(Icons.Outlined.BlurOn, null)
                    },
                    text = {
                        Text("模糊化部分UI组件")
                    },
                    state = rememberBooleanPreference(
                        key = "demoMode",
                        default = false
                    )
                )
            }
        }

        SettingItemCategory(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_video_title))
            }
        ) {
            // 洗脑循环
            val videoLoop = rememberBooleanPreference(
                key = "setting.videoLoop",
                default = false
            )
            SettingBooleanItem(
                state = videoLoop,
                icon = {
                    Icon(Icons.Outlined.Loop, null)
                },
                title = {
                    Text("洗脑循环")
                },
                text = {
                    Text("视频是否自动循环播放")
                }
            )

            // 自动播放
            val autoPlayVideo = rememberBooleanPreference(
                key = "setting.autoPlayVideo",
                default = true
            )
            SettingBooleanItem(
                icon = {
                    Icon(Icons.Outlined.PlayArrow, null)
                },
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_video_auto_start_title))
                },
                text = {
                    Text(text = stringResource(id = R.string.screen_setting_video_auto_start_subtitle))
                },
                state = autoPlayVideo
            )
            AnimatedVisibility(visible = autoPlayVideo.value) {
                val autoPlayOnWifi = rememberBooleanPreference(
                    key = "setting.autoPlayVideoOnWifi",
                    default = false
                )
                SettingBooleanItem(
                    icon = {
                        Icon(Icons.Outlined.Wifi, null)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.screen_setting_video_auto_wifi_title))
                    },
                    text = {
                        Text(text = stringResource(id = R.string.screen_setting_video_auto_wifi_subtitle))
                    },
                    state = autoPlayOnWifi
                )
            }
        }

        SettingItemCategory(
            title = {
                Text(text = "网络设置")
            }
        ) {
            val useDoH = rememberBooleanPreference(key = "setting.useDoH", default = false)
            SettingBooleanItem(
                state = useDoH,
                icon = {
                    Icon(Icons.Outlined.Dns, null)
                },
                title = {
                    Text("DoH")
                },
                text = {
                    Text("是否使用DoH解析域名")
                }
            )
        }

        SettingItemCategory(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_app_info_title))
            }
        ) {
            SettingLinkItem(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_app_about_title))
                },
                icon = {
                    Icon(Icons.Outlined.Copyright, null)
                },
                text = {
                    Text(text = "${stringResource(id = R.string.screen_setting_app_about_subtitle)}: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                }
            ) {
                navController.navigate("about")
            }

            SettingLinkItem(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_app_logger))
                },
                icon = {
                    Icon(Icons.Outlined.Book, null)
                },
                text = {}
            ) {
                navController.navigate("logger")
            }
        }
    }
}