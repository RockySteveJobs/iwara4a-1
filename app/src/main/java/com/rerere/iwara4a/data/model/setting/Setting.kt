package com.rerere.iwara4a.data.model.setting

data class Setting(
    val themeSetting: Theme
) {
    data class Theme(
        var mode: Int
    )
}