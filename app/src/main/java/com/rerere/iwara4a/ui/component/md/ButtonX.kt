package com.rerere.iwara4a.ui.component.md

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ButtonX(
    style: ButtonStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Crossfade(style) { buttonStyle ->
        when (buttonStyle) {
            ButtonStyle.Filled -> Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
            ButtonStyle.Outlined -> OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
            ButtonStyle.Text -> TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
            ButtonStyle.FilledTonal -> FilledTonalButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
            ButtonStyle.Elevated -> ElevatedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                content = content
            )
        }
    }
}

enum class ButtonStyle {
    Filled,
    Outlined,
    Text,
    FilledTonal,
    Elevated
}