package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.model.detail.video.unescapeJava
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar

fun String.toLink() = "https:" + unescapeJava(this).replace("\\/", "/")

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar(title = "Test")
        }
    ) { innerPadding ->
        val dp = LocalDensity.current
        var densityMult by remember {
            mutableStateOf(1f)
        }
        CompositionLocalProvider(LocalDensity provides Density(dp.density * densityMult)) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Outlined.Work, null)
                    Slider(
                        value = densityMult,
                        onValueChange = { densityMult = it },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Outlined.Work, null)
                }
            }
        }
    }
}