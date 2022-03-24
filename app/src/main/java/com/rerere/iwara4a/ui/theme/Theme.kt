package com.rerere.iwara4a.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val Colors.uiBackGroundColor
    get() = if (isLight) {
        Color.White
    } else {
        Color.Black
    }

@Composable
fun Iwara4aTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // MD2 Compat
        androidx.compose.material.MaterialTheme(
            colors = colorScheme.toLegacyColor(darkTheme),
            content = content
        )
    }
}