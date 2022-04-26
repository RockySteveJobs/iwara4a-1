package com.rerere.iwara4a.ui.screen.test

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.HorizontalPager
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar

@Composable
fun TestScreen() {
    Scaffold(
        topBar = {
            SimpleIwaraTopBar("Test")
        }
    ) { padding ->
        HorizontalPager(
            modifier = Modifier.padding(padding),
            count = 2
        ) { pageOut ->
            when(pageOut) {
                0 -> {
                    HorizontalPager(count = 4) {
                       LazyColumn {
                           items(100) {
                               Text(text = "Item A: $it")
                           }
                       }
                    }
                }
                1 -> {
                    HorizontalPager(count = 4) {
                        LazyColumn {
                            items(100) {
                                Text(text = "Item B: $it")
                            }
                        }
                    }
                }
            }
        }
    }
}