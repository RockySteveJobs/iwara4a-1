package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.component.BottomSheetDialog
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import kotlin.random.Random

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar("Test")
        }
    ) {
        Column {
            Button(onClick = {
                sharedPreferencesOf("session").edit {
                    putString("value", Random.nextInt().toString().repeat(10))
                }
            }) {
                Text(text = "破坏cookie")
            }
        }
    }
}