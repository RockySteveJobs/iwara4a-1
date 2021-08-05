package com.rerere.iwara4a.ui.screen.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alorma.settings.composables.SettingsGroup
import com.alorma.settings.composables.SettingsSwitch
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.ui.public.rememberBooleanPreferenceState

@Composable
fun SettingScreen(
    navController: NavController,
    settingViewModel: SettingViewModel = hiltViewModel()
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

@Composable
private fun Body(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsGroup(
            title = {
                Text(text = "视频设置")
            }
        ) {
            var autoPlayVideo by rememberBooleanPreferenceState(key = "setting.autoPlayVideo", init = true)
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
        }
    }
}