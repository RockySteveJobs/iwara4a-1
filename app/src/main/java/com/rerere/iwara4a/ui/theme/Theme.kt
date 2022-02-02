package com.rerere.iwara4a.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.alorma.compose.settings.storage.base.getValue
import com.alorma.compose.settings.storage.base.setValue
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.alorma.compose.settings.storage.preferences.rememberPreferenceIntSettingState
import com.rerere.iwara4a.ui.public.rememberIntPreference

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