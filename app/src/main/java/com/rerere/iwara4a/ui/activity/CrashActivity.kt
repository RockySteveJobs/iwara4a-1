package com.rerere.iwara4a.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.ui.util.plus

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stackTrace = intent.getStringExtra("stackTrace") ?: ""

        setContent {
            MaterialTheme {
                val decay = rememberSplineBasedDecay<Float>()
                val scrollBehavior = remember {
                    TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                        decayAnimationSpec = decay
                    )
                }
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text("APP Crashed")
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(Icons.Outlined.Close, null)
                                }
                            },
                            scrollBehavior = scrollBehavior
                        )
                    }
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding + PaddingValues(8.dp),
                    ) {
                        items(stackTrace.split("\n")) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}