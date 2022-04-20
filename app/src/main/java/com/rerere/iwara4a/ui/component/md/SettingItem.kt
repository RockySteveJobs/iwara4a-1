package com.rerere.iwara4a.ui.component.md

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LinkSettingItem(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onClick: () -> Unit
) {
    SettingItem(
        icon = icon,
        title = title,
        text = text,
        action = {},
        onClick = onClick
    )
}

@Composable
fun BooleanSettingItem(
    state: MutableState<Boolean>,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onChange: (Boolean) -> Unit = {}
) {
    SettingItem(
        icon = icon,
        title = title,
        text = text,
        onClick = {
            state.value = !state.value
        }
    ) {
        // TODO: 迁移到MD3 Switch
        Switch(
            checked = state.value,
            onCheckedChange = {
                state.value = it
                onChange(it)
            }
        )
    }
}

@Composable
fun Category(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProvideTextStyle(
            MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.primary)
        ) {
            title()
        }
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingItem(
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        icon()
        Column(
            modifier = Modifier.weight(1f)
        ) {
            ProvideTextStyle(
                MaterialTheme.typography.titleMedium
            ) {
                title()
            }
            ProvideTextStyle(
                MaterialTheme.typography.bodySmall
            ) {
                text()
            }
        }
        action()
    }
}

