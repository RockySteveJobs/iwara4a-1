package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.component.BottomSheetDialog
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar("Test")
        }
    ) {
        var showDialog by remember { mutableStateOf(false) }
        Button(
            onClick = {
                showDialog = true
            }
        ) {
            Text("Show Dialog")
        }
        if (showDialog) {
            BottomSheetDialog(
                onDismissRequest = {
                    showDialog = false
                }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(100){
                        Card {
                            Text(
                                text = "Test",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}