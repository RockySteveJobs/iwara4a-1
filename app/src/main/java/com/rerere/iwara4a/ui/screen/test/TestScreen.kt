package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.model.detail.video.unescapeJava
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.component.md.SliderPatch

fun String.toLink() = "https:" + unescapeJava(this).replace("\\/", "/")

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar(title = "Test")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            val currentDensity = LocalDensity.current
            var densityMultiplier by remember {
                mutableStateOf(1f)
            }
            Text("curr: ${currentDensity.density} | mul: $densityMultiplier")
            CompositionLocalProvider(
                LocalDensity provides Density(currentDensity.density * densityMultiplier)
            ) {
                Box(modifier = Modifier.requiredWidth(0.dp)) {
                    SliderPatch(
                        value = densityMultiplier,
                        onValueChange = { densityMultiplier = it }
                    )
                }
            }
        }
    }
}