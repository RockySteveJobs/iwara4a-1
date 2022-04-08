package com.rerere.iwara4a.ui.component.md

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ButtonToggleGroundPreview() {
    ButtonToggleGroup(
        currentSelected = 1,
        buttonAmount = 2,
        onClick = {}
    ) {
        when (it) {
            0 -> {
                Text(text = "测试2")
            }
            1 -> {
                Text(text = "测试")
            }
        }
    }
}

@Composable
fun ButtonToggleGroup(
    modifier: Modifier = Modifier,
    currentSelected: Int,
    onClick: (Int) -> Unit,
    buttonAmount: Int,
    content: @Composable RowScope.(Int) -> Unit
) {
    val colors = ButtonDefaults.buttonColors()
    val elevation = ButtonDefaults.buttonElevation()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(buttonAmount) { index ->
            val enabled = index == currentSelected
            val interactionSource = remember { MutableInteractionSource() }
            val containerColor = colors.containerColor(enabled).value
            val contentColor = colors.contentColor(enabled).value
            val shadowElevation = elevation.shadowElevation(enabled, interactionSource).value
            val tonalElevation = elevation.tonalElevation(enabled, interactionSource).value
            Surface(
                color = containerColor,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                onClick = {
                    onClick(index)
                },
                shape = when {
                    index == 0 -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
                    index == buttonAmount - 1 -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
                    else -> Shapes.None
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(ButtonDefaults.ContentPadding)
                ) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}