package com.rerere.iwara4a.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.ui.component.rememberStringPreference

@Composable
fun Iwara4aTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val theme by rememberStringPreference(
        keyName = "theme",
        defaultValue = "system",
        initialValue = "system"
    )
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && theme == "system") {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        when (theme) {
            "pink" -> pinkColorScheme(darkTheme)
            "blue" -> blueColorScheme(darkTheme)
            "green" -> greenColorScheme(darkTheme)
            else -> if (darkTheme) darkColorScheme() else lightColorScheme()
        }
    }
    SideEffect {
        if (BuildConfig.DEBUG) {
            println("primary = Color(0x${colorScheme.primary.toHex()})")
            println("onPrimary = Color(0x${colorScheme.onPrimary.toHex()})")
            println("primaryContainer = Color(0x${colorScheme.primaryContainer.toHex()})")
            println("onPrimaryContainer = Color(0x${colorScheme.onPrimaryContainer.toHex()})")
            println("inversePrimary = Color(0x${colorScheme.inversePrimary.toHex()})")
            println("secondary = Color(0x${colorScheme.secondary.toHex()})")
            println("onSecondary = Color(0x${colorScheme.onSecondary.toHex()})")
            println("secondaryContainer = Color(0x${colorScheme.secondaryContainer.toHex()})")
            println("onSecondaryContainer = Color(0x${colorScheme.onSecondaryContainer.toHex()})")
            println("tertiary = Color(0x${colorScheme.tertiary.toHex()})")
            println("onTertiary = Color(0x${colorScheme.onTertiary.toHex()})")
            println("tertiaryContainer = Color(0x${colorScheme.tertiaryContainer.toHex()})")
            println("onTertiaryContainer = Color(0x${colorScheme.onTertiaryContainer.toHex()})")
            println("background = Color(0x${colorScheme.background.toHex()})")
            println("onBackground = Color(0x${colorScheme.onBackground.toHex()})")
            println("surface = Color(0x${colorScheme.surface.toHex()})")
            println("onSurface = Color(0x${colorScheme.onSurface.toHex()})")
            println("surfaceVariant = Color(0x${colorScheme.surfaceVariant.toHex()})")
            println("onSurfaceVariant = Color(0x${colorScheme.onSurfaceVariant.toHex()})")
            println("surfaceTint = Color(0x${colorScheme.surfaceTint.toHex()})")
            println("inverseSurface = Color(0x${colorScheme.inverseSurface.toHex()})")
            println("inverseOnSurface = Color(0x${colorScheme.inverseOnSurface.toHex()})")
            println("error = Color(0x${colorScheme.error.toHex()})")
            println("onError = Color(0x${colorScheme.onError.toHex()})")
            println("errorContainer = Color(0x${colorScheme.errorContainer.toHex()})")
            println("onErrorContainer = Color(0x${colorScheme.onErrorContainer.toHex()})")
            println("outline = Color(0x${colorScheme.outline.toHex()})")
        }
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