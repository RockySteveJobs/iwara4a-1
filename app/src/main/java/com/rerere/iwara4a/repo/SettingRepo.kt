package com.rerere.iwara4a.repo

import androidx.core.content.edit
import com.rerere.iwara4a.model.setting.Setting
import com.rerere.iwara4a.sharedPreferencesOf

class SettingRepo {
    var setting: Setting = sharedPreferencesOf("setting").let {
        // Theme
        val themeMode = it.getInt("theme.mode", 0)
        val theme = Setting.Theme(
            mode = themeMode
        )

        // Return
        Setting(
            themeSetting = theme
        )
    }

    fun save() {
        sharedPreferencesOf("setting").edit {
            putInt("theme.mode", setting.themeSetting.mode)
        }
    }
}