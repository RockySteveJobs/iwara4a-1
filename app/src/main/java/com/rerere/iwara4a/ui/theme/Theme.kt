package com.rerere.iwara4a.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

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
fun Iwara4aTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette.copy(
            primary = CustomColor
        )
    } else {
        LightColorPalette.copy(
            primary = CustomColor
        )
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> darkColorScheme()
            else -> lightColorScheme()
        }
    ) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}