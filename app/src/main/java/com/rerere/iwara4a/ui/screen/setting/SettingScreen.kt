package com.rerere.iwara4a.ui.screen.setting

import android.widget.Toast
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import androidx.navigation.NavController
import com.alorma.settings.composables.SettingsGroup
import com.alorma.settings.composables.SettingsMenuLink
import com.alorma.settings.composables.SettingsSwitch
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.public.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.public.rememberBooleanPreference
import com.rerere.iwara4a.ui.theme.CustomColor
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.stringResource
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun SettingScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar(navController, stringResource(id = R.string.screen_setting_topbar_title))
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
            Text(text = stringResource(id = R.string.screen_setting_personalize_title))
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
                    Text(text = stringResource(id = R.string.screen_setting_personalize_follow_system_dark_title))
                },
                subtitle = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_follow_system_dark_subtitle))
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
                        Text(text = stringResource(id = R.string.screen_setting_personalize_darkmode_title))
                    },
                    subtitle = {
                        Text(text = stringResource(id = R.string.screen_setting_personalize_darkmode_subtitle))
                    },
                    checked = darkMode,
                    onCheckedChange = {
                        darkMode = it
                    }
                )
            }
            val themeColor = rememberMaterialDialogState()
            MaterialDialog(
                dialogState = themeColor,
                buttons = {
                    positiveButton(stringResource(id = R.string.confirm_button)) {
                        themeColor.hide()
                    }
                }
            ) {
                title(stringResource(id = R.string.screen_setting_personalize_theme_choose))
                colorChooser(colors = ColorPalette.Primary.toMutableList().apply {
                    add(0, PINK)
                }) { color ->
                    println("Set Primary = ${color.toArgb()}")
                    sharedPreferencesOf("themeColor").edit {
                        putFloat("r", color.red)
                        putFloat("g", color.green)
                        putFloat("b", color.blue)
                        putFloat("a", color.alpha)
                    }
                    CustomColor = color
                }
            }
            SettingsMenuLink(
                title = {
                    Text(text = stringResource(id = R.string.screen_setting_personalize_theme_title))
                },
                icon = {
                    Icon(Icons.Default.Brush, null)
                }
            ) {
                themeColor.show()
            }
            var preventScreenCaptcha by rememberBooleanPreference(
                keyName = "setting.preventscreencaptcha",
                defaultValue = false,
                initialValue = false
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
                checked = preventScreenCaptcha,
                onCheckedChange = {
                    preventScreenCaptcha = it
                    Toast.makeText(context, context.stringResource(id = R.string.screen_setting_personalize_preventscreen_reboot), Toast.LENGTH_SHORT).show()
                }
            )
        }

        SettingsGroup(
            title = {
                Text(text = stringResource(id = R.string.screen_setting_video_title))
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
                    Text(text = stringResource(id = R.string.screen_setting_video_auto_start_title))
                },
                subtitle = {
                    Text(text = stringResource(id = R.string.screen_setting_video_auto_start_subtitle))
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
                        Text(text = stringResource(id = R.string.screen_setting_video_auto_wifi_title))
                    },
                    subtitle = {
                        Text(text = stringResource(id = R.string.screen_setting_video_auto_wifi_subtitle))
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
                Text(text = stringResource(id = R.string.screen_setting_comment_title))
            }
        ) {
            var showCommentTail by rememberBooleanPreference(
                keyName = "setting.tail",
                initialValue = true
            )
            SettingsSwitch(
                title = {
                    Text(text = "评论广告小尾巴")
                },
                subtitle = {
                    Text(text = "最好还是开着吧，多吸引些用户")
                },
                icon = {
                    Icon(Icons.Default.Comment, null)
                },
                checked = showCommentTail,
                onCheckedChange = {
                    showCommentTail = !showCommentTail
                }
            )
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