package com.rerere.iwara4a.ui.screen.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.repo.SettingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepo: SettingRepo
) : ViewModel() {
    var themeMode by mutableStateOf(settingRepo.setting.themeSetting.mode)
}