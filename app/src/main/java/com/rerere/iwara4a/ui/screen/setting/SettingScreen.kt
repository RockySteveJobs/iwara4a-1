package com.rerere.iwara4a.ui.screen.setting

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.component.md.BooleanSettingItem
import com.rerere.iwara4a.ui.component.md.ButtonToggleGroup
import com.rerere.iwara4a.ui.component.md.Category
import com.rerere.iwara4a.ui.component.md.LinkSettingItem
import com.rerere.iwara4a.ui.component.rememberBooleanPreference
import com.rerere.iwara4a.ui.component.rememberIntPreference
import com.rerere.iwara4a.ui.component.rememberStringPreference
import com.rerere.iwara4a.ui.local.LocalNavController
import com.tencent.mmkv.MMKV
import java.util.*

@Composable
fun SettingScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar(stringResource(id = R.string.screen_setting_topbar_title))
        }
    ) {
        Body()
    }
}

@Composable
private fun Body() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
    ) {
        Category(
            title = {
                Text(
                    text = stringResource(id = R.string.screen_setting_personalize_title)
                )
            }
        ) {
            // 主题
            var theme by rememberStringPreference(
                keyName = "theme",
                defaultValue = "system",
                initialValue = "system"
            )
            var expandTheme by remember {
                mutableStateOf(false)
            }
            LinkSettingItem(
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
                    Icon(Icons.Rounded.Palette, null)
                }
            )
            AnimatedVisibility(expandTheme) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var nightMode by remember {
                        mutableStateOf(MMKV.defaultMMKV().decodeInt("nightMode"))
                    }
                    ButtonToggleGroup(
                        currentSelected = nightMode,
                        onClick = {
                            nightMode = it
                            MMKV.defaultMMKV().encode("nightMode", it)
                            when(it){
                                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                            (context as Activity).recreate()
                        },
                        buttonAmount = 3
                    ) {
                        when(it) {
                            0 -> {
                                Icon(Icons.Rounded.Android, null)
                                Text(text = "自动")
                            }
                            1 -> {
                                Icon(Icons.Rounded.LightMode, null)
                                Text(text = "亮色")
                            }
                            2 -> {
                                Icon(Icons.Rounded.DarkMode, null)
                                Text(text = "暗色")
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                theme = "system"
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                                    Toast.makeText(
                                        context,
                                        "本功能需要 Android 12 以上",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.Android, null)
                            Text("壁纸取色")
                            AnimatedVisibility(theme == "system") {
                                Icon(Icons.Rounded.Check, null)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable {
                                    theme = "pink"
                                }
                                .background(Color(0xff944746)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.animation.AnimatedVisibility(theme == "pink") {
                                Icon(Icons.Rounded.Check, null)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable {
                                    theme = "blue"
                                }
                                .background(Color(0xff1d6392)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.animation.AnimatedVisibility(theme == "blue") {
                                Icon(Icons.Rounded.Check, null)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable {
                                    theme = "green"
                                }
                                .background(Color(0xff2a6a3d)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.animation.AnimatedVisibility(theme == "green") {
                                Icon(Icons.Rounded.Check, null)
                            }
                        }
                    }
                }
            }

            // 阻止多任务预览
            BooleanSettingItem(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_scraping_title))
                },
                icon = {
                    Icon(Icons.Default.ScreenShare, null)
                },
                text = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_preventscreen_subtitle))
                },
                state = rememberBooleanPreference(
                    keyName = "setting.preventscreencaptcha",
                    defaultValue = false,
                    initialValue = false
                )
            ) {
                sharedPreferencesOf("setting").edit {
                    putBoolean("preventscreencaptcha", it)
                }
            }

            // 演示模式
            if (Locale.getDefault().language == Locale.CHINA.language) {
                BooleanSettingItem(
                    title = {
                        Text("演示模式")
                    },
                    icon = {
                        Icon(Icons.Default.BlurOn, null)
                    },
                    text = {
                        Text("模糊化部分UI组件")
                    },
                    state = rememberBooleanPreference(
                        keyName = "demoMode",
                        defaultValue = false,
                        initialValue = false
                    )
                )
            }
        }

        Category(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_video_title))
            }
        ) {
            // 自动播放
            val autoPlayVideo = rememberBooleanPreference(
                keyName = "setting.autoPlayVideo",
                defaultValue = true,
                initialValue = true
            )
            BooleanSettingItem(
                icon = {
                    Icon(Icons.Default.PlayArrow, null)
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
                    keyName = "setting.autoPlayVideoOnWifi",
                    defaultValue = false,
                    initialValue = false
                )
                BooleanSettingItem(
                    icon = {
                        Icon(Icons.Default.Wifi, null)
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

        Category(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_app_info_title))
            }
        ) {
            LinkSettingItem(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_app_about_title))
                },
                icon = {
                    Icon(Icons.Default.Copyright, null)
                },
                text = {
                    Text(text = "${stringResource(id = R.string.screen_setting_app_about_subtitle)}: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                }
            ) {
                navController.navigate("about")
            }

            LinkSettingItem(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_app_logger))
                },
                icon = {
                    Icon(Icons.Default.Book, null)
                },
                text = {}
            ) {
                navController.navigate("logger")
            }
        }
    }
}