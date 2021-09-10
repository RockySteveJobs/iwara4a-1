package com.rerere.iwara4a.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = CustomColor,
    secondary = Color(0xffaa0529),
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = CustomColor,
    secondary = Color(0xffaa0529),
    onSecondary = Color.White
)

val Colors.uiBackGroundColor
    get() = if (isLight) {
        Color.White
    } else {
        Color.Black
    }

@Composable
fun Iwara4aTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette.copy(
            primary = CustomColor
        )
    } else {
        LightColorPalette.copy(
            primary = CustomColor
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}