package com.rerere.iwara4a.ui.screen.test

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.recomposeHighlighter

@Composable
fun TestScreen() {
    val scrollBehavior = remember {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    }
    val view = LocalView.current
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
                .fillMaxSize(),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    }
                ) {
                    Text(text = "LONG_PRESS")
                }
            }

            item {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                ) {
                    Text(text = "VIRTUAL_KEY")
                }
            }

            item {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                ) {
                    Text(text = "KEYBOARD_TAP")
                }
            }

            item {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    }
                ) {
                    Text(text = "CLOCK_TICK")
                }
            }

            item {
                Button(
                    onClick = {
                        view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                    }
                ) {
                    Text(text = "CONTEXT_CLICK")
                }
            }
        }
    }
}