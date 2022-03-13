package com.rerere.iwara4a.ui.theme

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.component.rememberIntPreference

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
    val themeMode by rememberIntPreference(
        keyName = "setting.themeMode",
        defaultValue = 0,
        initialValue = 0
    )
    val darkTheme = when(themeMode){
        0 -> isSystemInDarkTheme()
        1 -> false
        2 -> true
        else -> error("unknown mode: $themeMode")
    }
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }.animate()
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

@Composable
fun ColorScheme.animate(): ColorScheme {
    return this.copy(
        background = animateColorAsState(background).value,
        onBackground = animateColorAsState(onBackground).value,

        tertiary = animateColorAsState(tertiary).value,
        onTertiary = animateColorAsState(onTertiary).value,
        tertiaryContainer = animateColorAsState(tertiaryContainer).value,

        surface = animateColorAsState(surface).value,
        onSurface = animateColorAsState(onSurface).value,
        surfaceVariant = animateColorAsState(surfaceVariant).value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant).value,
        inverseSurface = animateColorAsState(inverseSurface).value,
        inverseOnSurface = animateColorAsState(inverseOnSurface).value,

        primary = animateColorAsState(primary).value,
        onPrimary = animateColorAsState(onPrimary).value,
        primaryContainer = animateColorAsState(primaryContainer).value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer).value,

        secondary = animateColorAsState(secondary).value,
        onSecondary = animateColorAsState(onSecondary).value,
        secondaryContainer = animateColorAsState(secondaryContainer).value,
        onSecondaryContainer = animateColorAsState(onSecondaryContainer).value
    )
}