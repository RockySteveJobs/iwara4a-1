package com.rerere.iwara4a.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.rerere.iwara4a.sharedPreferencesOf

val PINK : Color = Color(0xfff45a8d)
val BACKGROUND = Color(0xFFF2F3F5)

var CustomColor by mutableStateOf(
    sharedPreferencesOf("themeColor").let {
        if (it.contains("r")) {
            Color(
                red = it.getFloat("r", PINK.red),
                green = it.getFloat("g", PINK.green),
                blue = it.getFloat("b", PINK.blue),
                alpha = it.getFloat("a", PINK.alpha)
            )
        } else PINK
    }
)