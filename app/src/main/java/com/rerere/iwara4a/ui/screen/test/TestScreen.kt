package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.recomposeHighlighter

@Composable
fun TestScreen() {
    val scrollBehavior = remember {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Md3TopBar(
                modifier = Modifier.recomposeHighlighter(),
                title = { Text("Test") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(100) {
                Card {
                    Text(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        text = "Test"
                    )
                }
            }
        }
    }
}