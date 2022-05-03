package com.rerere.iwara4a.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.rerere.iwara4a.ui.local.LocalDarkMode
import me.rerere.compose_setting.preference.rememberIntPreference
import me.rerere.md3compat.Md3CompatTheme

@Composable
fun Iwara4aTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val nightMode by rememberIntPreference(key = "nightMode", default = 0)
    val darkTheme = when(nightMode) {
        0 -> isSystemInDarkTheme()
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }
    CompositionLocalProvider(LocalDarkMode provides darkTheme) {
        ApplyBarColor()
        Md3CompatTheme(
            darkTheme = darkTheme,
            // colorScheme = if(darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context),
            typography = Typography
        ) {
            // MD2 Compat
            androidx.compose.material.MaterialTheme(
                colors = MaterialTheme.colorScheme.toLegacyColor(darkTheme),
                content = content
            )
        }
    }
}

@Composable
fun ApplyBarColor(darkTheme: Boolean = LocalDarkMode.current) {
    val view = LocalView.current
    val activity = LocalContext.current as Activity
    SideEffect {
        (view.context as Activity).window.apply {
            statusBarColor = Color.Transparent.toArgb()
            navigationBarColor = Color.Transparent.toArgb()
        }
        WindowCompat.getInsetsController(activity.window,view).apply {
            isAppearanceLightNavigationBars = !darkTheme
            isAppearanceLightStatusBars = !darkTheme
        }
    }
}