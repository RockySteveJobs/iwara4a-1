package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.util.memSaver

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar("Test")
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding
        ) {
            item {
                var counter by rememberSaveable(saver = memSaver()) {
                    mutableStateOf(0)
                }
                Button(onClick = { counter++ }) {
                    Text("Add: $counter")
                }
            }

            items(100) {
                Text("测试")
            }
        }
    }
}