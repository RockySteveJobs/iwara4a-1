package com.rerere.iwara4a.model.setting

data class Setting(
    val themeSetting: Theme
){
    data class Theme(
        var mode: Int
    )
}