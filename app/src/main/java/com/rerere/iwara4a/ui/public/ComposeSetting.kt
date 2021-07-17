package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun SettingGroup(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable (ColumnScope) -> Unit
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier.padding(16.dp, 8.dp)){
            CompositionLocalProvider(LocalTextStyle provides TextStyle.Default.copy(
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Light
            )) {
                title()
            }
        }
        content(this)
    }
}

@Composable
fun SettingItemMultiChoice(
    modifier: Modifier = Modifier,
    choice: String,
    choiceList: List<String>,
    onChoiceChange: (String) -> Unit
){
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        ConstraintLayout(modifier = Modifier.padding(4.dp)) {
            val (iconRef, titleRef, descriptionRef, choiceRef) = createRefs()
            // TODO
        }
    }
}