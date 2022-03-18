package com.rerere.iwara4a.ui.screen.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.component.rememberIntPreference

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
            // 0 = follow system
            // 1 = light mode
            // 2 = dark mode
            var themeMode by rememberIntPreference(
                keyName = "setting.themeMode",
                defaultValue = 0,
                initialValue = 0
            )
            var selectingTheme by remember {
                mutableStateOf(false)
            }
            SettingsMenuLink(
                icon = {
                    Icon(Icons.Default.DarkMode, null)
                },
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_theme_mode))
                    DropdownMenu(
                        expanded = selectingTheme,
                        onDismissRequest = { selectingTheme = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                themeMode = 0
                                selectingTheme = false
                            },
                            text = {
                                Text(text = stringResource(R.string.theme_auto))
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                themeMode = 1
                                selectingTheme = false
                            }, text = {
                                Text(text = stringResource(R.string.theme_light))
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                themeMode = 2
                                selectingTheme = false
                            },
                            text = {
                                Text(text = stringResource(R.string.theme_dark))
                            }
                        )
                    }
                },
                subtitle = {
                    when (themeMode) {
                        0 -> Text(text = stringResource(R.string.theme_auto))
                        1 -> Text(text = stringResource(R.string.theme_light))
                        2 -> Text(text = stringResource(R.string.theme_dark))
                    }
                },
                onClick = {
                    selectingTheme = true
                }
            )


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

            if(BuildConfig.DEBUG) {
                SettingsMenuLink(
                    title = {
                        Text(text = stringResource(id = R.string.screen_setting_app_debug_title))
                    },
                    icon = {
                        Icon(Icons.Default.DeveloperMode, null)
                    },
                    subtitle = {
                        Text(text = stringResource(id = R.string.screen_setting_app_debug_subtitle))
                    }
                ) {
                    navController.navigate("dev")
                }
            }
        }
    }
}