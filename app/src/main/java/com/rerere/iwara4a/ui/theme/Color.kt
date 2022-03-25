package com.rerere.iwara4a.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val PINK: Color = Color(0xfff45a8d)

// PINK Color Scheme
fun pinkColorScheme(isDark: Boolean = false): ColorScheme = if (!isDark) {
    lightColorScheme(
        primary = Color(0xff944746),
        onPrimary = Color(0xffffffff),
        primaryContainer = Color(0xffffdad6),
        onPrimaryContainer = Color(0xff3d0509),
        inversePrimary = Color(0xffffb3b0),
        secondary = Color(0xff775655),
        onSecondary = Color(0xffffffff),
        secondaryContainer = Color(0xffffdad7),
        onSecondaryContainer = Color(0xff2c1514),
        tertiary = Color(0xff79591a),
        onTertiary = Color(0xffffffff),
        tertiaryContainer = Color(0xffffdea7),
        onTertiaryContainer = Color(0xff281900),
        background = Color(0xfffffbfa),
        onBackground = Color(0xff201a1a),
        surface = Color(0xfffffbfa),
        onSurface = Color(0xff201a1a),
        surfaceVariant = Color(0xfff5dddc),
        onSurfaceVariant = Color(0xff524342),
        surfaceTint = Color(0xff944746),
        inverseSurface = Color(0xff362f2e),
        inverseOnSurface = Color(0xfffbeeec),
        error = Color(0xffb3261e),
        onError = Color(0xffffffff),
        errorContainer = Color(0xfff9dedc),
        onErrorContainer = Color(0xff410e0b),
        outline = Color(0xff847270)
    )
} else {
    darkColorScheme(
        primary = Color(0xffffb3b0),
        onPrimary = Color(0xff5a1a1b),
        primaryContainer = Color(0xff763030),
        onPrimaryContainer = Color(0xffffdad6),
        inversePrimary = Color(0xff944746),
        secondary = Color(0xffe6bdba),
        onSecondary = Color(0xff442a28),
        secondaryContainer = Color(0xff5d3f3e),
        onSecondaryContainer = Color(0xffffdad7),
        tertiary = Color(0xffebc077),
        onTertiary = Color(0xff432c00),
        tertiaryContainer = Color(0xff5f4102),
        onTertiaryContainer = Color(0xffffdea7),
        background = Color(0xff201a1a),
        onBackground = Color(0xffecdfde),
        surface = Color(0xff201a1a),
        onSurface = Color(0xffecdfde),
        surfaceVariant = Color(0xff524342),
        onSurfaceVariant = Color(0xffd7c2c0),
        surfaceTint = Color(0xffffb3b0),
        inverseSurface = Color(0xffecdfde),
        inverseOnSurface = Color(0xff362f2e),
        error = Color(0xfff2b8b5),
        onError = Color(0xff601410),
        errorContainer = Color(0xff8c1d18),
        onErrorContainer = Color(0xfff2b8b5),
        outline = Color(0xffa08c8b)
    )
}

// Blue Color Scheme
fun blueColorScheme(isDark: Boolean = false) = if (!isDark) {
    lightColorScheme(
        primary = Color(0xff1d6392),
        onPrimary = Color(0xffffffff),
        primaryContainer = Color(0xffcbe5ff),
        onPrimaryContainer = Color(0xff001d31),
        inversePrimary = Color(0xff90ccff),
        secondary = Color(0xff51606f),
        onSecondary = Color(0xffffffff),
        secondaryContainer = Color(0xffd4e4f6),
        onSecondaryContainer = Color(0xff0d1d29),
        tertiary = Color(0xff695587),
        onTertiary = Color(0xffffffff),
        tertiaryContainer = Color(0xffeedcff),
        onTertiaryContainer = Color(0xff23113f),
        background = Color(0xfffcfcff),
        onBackground = Color(0xff1a1c1e),
        surface = Color(0xfffcfcff),
        onSurface = Color(0xff1a1c1e),
        surfaceVariant = Color(0xffdde3ea),
        onSurfaceVariant = Color(0xff41474d),
        surfaceTint = Color(0xff1d6392),
        inverseSurface = Color(0xff2f3032),
        inverseOnSurface = Color(0xfff0f0f4),
        error = Color(0xffb3261e),
        onError = Color(0xffffffff),
        errorContainer = Color(0xfff9dedc),
        onErrorContainer = Color(0xff410e0b),
        outline = Color(0xff71767d)
    )
} else {
    darkColorScheme(
        primary = Color(0xff90ccff),
        onPrimary = Color(0xff003352),
        primaryContainer = Color(0xff004b75),
        onPrimaryContainer = Color(0xffcbe5ff),
        inversePrimary = Color(0xff1d6392),
        secondary = Color(0xffb8c9da),
        onSecondary = Color(0xff23323f),
        secondaryContainer = Color(0xff394857),
        onSecondaryContainer = Color(0xffd4e4f6),
        tertiary = Color(0xffd4bcf5),
        onTertiary = Color(0xff392755),
        tertiaryContainer = Color(0xff503e6d),
        onTertiaryContainer = Color(0xffeedcff),
        background = Color(0xff1a1c1e),
        onBackground = Color(0xffe2e2e6),
        surface = Color(0xff1a1c1e),
        onSurface = Color(0xffe2e2e6),
        surfaceVariant = Color(0xff41474d),
        onSurfaceVariant = Color(0xffc2c7ce),
        surfaceTint = Color(0xff90ccff),
        inverseSurface = Color(0xffe2e2e6),
        inverseOnSurface = Color(0xff2f3032),
        error = Color(0xfff2b8b5),
        onError = Color(0xff601410),
        errorContainer = Color(0xff8c1d18),
        onErrorContainer = Color(0xfff2b8b5),
        outline = Color(0xff8c9198)
    )
}

// GREEN Color Scheme
fun greenColorScheme(dark: Boolean = false) = if (!dark) {
    lightColorScheme(
        primary = Color(0xff2a6a3d),
        onPrimary = Color(0xffffffff),
        primaryContainer = Color(0xffadf3b8),
        onPrimaryContainer = Color(0xff002109),
        inversePrimary = Color(0xff93d69e),
        secondary = Color(0xff516352),
        onSecondary = Color(0xffffffff),
        secondaryContainer = Color(0xffd3e8d2),
        onSecondaryContainer = Color(0xff0e1f12),
        tertiary = Color(0xff1c6774),
        onTertiary = Color(0xffffffff),
        tertiaryContainer = Color(0xffaaedfb),
        onTertiaryContainer = Color(0xff001f25),
        background = Color(0xfffbfdf7),
        onBackground = Color(0xff1a1c19),
        surface = Color(0xfffbfdf7),
        onSurface = Color(0xff1a1c19),
        surfaceVariant = Color(0xffdde4da),
        onSurfaceVariant = Color(0xff414941),
        surfaceTint = Color(0xff2a6a3d),
        inverseSurface = Color(0xff2e312e),
        inverseOnSurface = Color(0xfff0f2ec),
        error = Color(0xffb3261e),
        onError = Color(0xffffffff),
        errorContainer = Color(0xfff9dedc),
        onErrorContainer = Color(0xff410e0b),
        outline = Color(0xff70786f)
    )
} else {
    darkColorScheme(
        primary = Color(0xff93d69e),
        onPrimary = Color(0xff003916),
        primaryContainer = Color(0xff0a5227),
        onPrimaryContainer = Color(0xffadf3b8),
        inversePrimary = Color(0xff2a6a3d),
        secondary = Color(0xffb7ccb7),
        onSecondary = Color(0xff233426),
        secondaryContainer = Color(0xff394b3b),
        onSecondaryContainer = Color(0xffd3e8d2),
        tertiary = Color(0xff8ed1df),
        onTertiary = Color(0xff00363e),
        tertiaryContainer = Color(0xff004e59),
        onTertiaryContainer = Color(0xffaaedfb),
        background = Color(0xff1a1c19),
        onBackground = Color(0xffe2e3de),
        surface = Color(0xff1a1c19),
        onSurface = Color(0xffe2e3de),
        surfaceVariant = Color(0xff414941),
        onSurfaceVariant = Color(0xffc1c8be),
        surfaceTint = Color(0xff93d69e),
        inverseSurface = Color(0xffe2e3de),
        inverseOnSurface = Color(0xff2e312e),
        error = Color(0xfff2b8b5),
        onError = Color(0xff601410),
        errorContainer = Color(0xff8c1d18),
        onErrorContainer = Color(0xfff2b8b5),
        outline = Color(0xff8b9389)
    )
}

/**
 * 将MD3颜色转换为MD2颜色，方便兼容MD2的组件
 */
fun ColorScheme.toLegacyColor(isDark: Boolean = false): Colors = if (!isDark) {
    lightColors(
        primary = this.primary,
        primaryVariant = this.primary,
        onPrimary = this.onPrimary,
        secondary = this.secondary,
        secondaryVariant = this.secondary,
        onSecondary = this.onSecondary,
        background = this.background,
        onBackground = this.onBackground,
        error = this.error,
        onError = this.onError,
        surface = this.surface,
        onSurface = this.onSurface
    )
} else {
    darkColors(
        primary = this.primary,
        primaryVariant = this.primary,
        onPrimary = this.onPrimary,
        secondary = this.secondary,
        secondaryVariant = this.secondary,
        onSecondary = this.onSecondary,
        background = this.background,
        onBackground = this.onBackground,
        error = this.error,
        onError = this.onError,
        surface = this.surface,
        onSurface = this.onSurface
    )
}

fun Color.toHex(): String = toArgb().toUInt().toString(16)