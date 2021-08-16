package com.rerere.iwara4a.ui.public

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.MaterialFadeThrough

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoundedChip(
    modifier: Modifier = Modifier,
    selected: Boolean,
    borderColor: Color = MaterialTheme.colors.primary,
    unSelectedBackgroundColor: Color = MaterialTheme.colors.surface,
    selectedBackgroundColor: Color = borderColor,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    MaterialFadeThrough(modifier = Modifier.padding(4.dp), targetState = selected) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.5.dp, borderColor),
            color = if (it) selectedBackgroundColor else unSelectedBackgroundColor
        ) {
            Box(modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(8.dp)) {
                content()
            }
        }
    }
}