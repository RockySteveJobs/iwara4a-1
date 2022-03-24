package com.rerere.iwara4a.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val PINK : Color = Color(0xfff45a8d)

/**
 * 将MD3颜色转换为MD2颜色，方便兼容MD2的组件
 */
fun ColorScheme.toLegacyColor(isDark: Boolean = false): Colors = if(!isDark){
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