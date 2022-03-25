package com.rerere.iwara4a.ui.screen.setting

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.alorma.compose.settings.ui.internal.*
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.component.rememberIntPreference
import com.rerere.iwara4a.ui.component.rememberStringPreference
import com.rerere.iwara4a.ui.theme.PINK
import kotlin.math.PI

@Composable
fun SettingScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar(stringResource(id = R.string.screen_setting_topbar_title))
        }
    ) {
        Box(modifier = Modifier.navigationBarsPadding()) {
            Body(navController)
        }
    }
}

@Composable
private fun Body(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsGroup(
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
            SettingsMenuLink(
                title = {
                    Text(stringResource(R.string.screen_setting_personalize_theme_mode))
                },
                subtitle = {
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
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                                Toast.makeText(context, "本功能需要 Android 12 以上", Toast.LENGTH_SHORT).show()
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
                    ){
                        androidx.compose.animation.AnimatedVisibility(theme == "green") {
                            Icon(Icons.Rounded.Check, null)
                        }
                    }
                }
            }

            // 阻止多任务预览
            val preventScreenCaptcha = rememberPreferenceBooleanSettingState(
                key = "setting.preventscreencaptcha",
                defaultValue = false,
            )
            SettingsSwitch(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_scraping_title))
                },
                icon = {
                    Icon(Icons.Default.ScreenShare, null)
                },
                subtitle = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_preventscreen_subtitle))
                },
                state = preventScreenCaptcha,
            )
        }

        SettingsGroup(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_video_title))
            }
        ) {
            val autoPlayVideo = rememberPreferenceBooleanSettingState(
                key = "setting.autoPlayVideo",
                defaultValue = true
            )
            SettingsSwitch(
                icon = {
                    Icon(Icons.Default.PlayArrow, null)
                },
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_video_auto_start_title))
                },
                subtitle = {
                    Text(text = stringResource(id = R.string.screen_setting_video_auto_start_subtitle))
                },
                state = autoPlayVideo
            )
            AnimatedVisibility(visible = autoPlayVideo.value) {
                val autoPlayOnWifi = rememberPreferenceBooleanSettingState(
                    key = "setting.autoPlayVideoOnWifi",
                    defaultValue = false
                )
                SettingsSwitch(
                    icon = {
                        Icon(Icons.Default.Wifi, null)
                    },
                    title = {
                        Text(text = stringResource(id = R.string.screen_setting_video_auto_wifi_title))
                    },
                    subtitle = {
                        Text(text = stringResource(id = R.string.screen_setting_video_auto_wifi_subtitle))
                    },
                    state = autoPlayOnWifi
                )
            }
        }

        SettingsGroup(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_app_info_title))
            }
        ) {
            SettingsMenuLink(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_app_about_title))
                },
                icon = {
                    Icon(Icons.Default.Copyright, null)
                },
                subtitle = {
                    Text(text = "${stringResource(id = R.string.screen_setting_app_about_subtitle)}: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                }
            ) {
                navController.navigate("about")
            }

            SettingsMenuLink(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_app_logger))
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