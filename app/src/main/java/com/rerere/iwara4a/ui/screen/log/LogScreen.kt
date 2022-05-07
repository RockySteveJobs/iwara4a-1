package com.rerere.iwara4a.ui.screen.log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.util.plus
import com.rerere.iwara4a.util.LogEntry
import com.rerere.iwara4a.util.format

@Composable
fun LogScreen() {
    val context = LocalContext.current
    val content by produceState(initialValue = emptyList()) {
        val file = context.cacheDir
            .resolve("logs")
            .resolve(
                DateFileNameGenerator()
                    .generateFileName(0, System.currentTimeMillis())
            )
        if (file.exists()) {
            value = file.readLines()
                .filter {
                    it.isNotEmpty()
                }.map {
                    try {
                        LogEntry.fromString(it.trim())
                    } catch (e: Exception) {
                        LogEntry(
                            message = it
                        )
                    }
                }
        }
    }
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text("Log")
                },
                navigationIcon = {
                    BackIcon()
                },
                actions = {
                    IconButton(
                        onClick = {
                            val file = context.cacheDir
                                .resolve("logs")
                                .resolve(
                                    DateFileNameGenerator()
                                        .generateFileName(0, System.currentTimeMillis())
                                )
                            if (file.exists()) {
                                file.writeText("")
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Delete, null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
                    + WindowInsets.navigationBars.asPaddingValues()
                    + PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(content) {
                LogEntry(it)
            }
        }
    }
}

@Composable
private fun LogEntry(entry: LogEntry) {
    Card {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = entry.time.format(true)
                )
                Text(
                    text = entry.thread
                )
                Text(
                    text = LogLevel.getLevelName(entry.level)
                )
            }

            Divider()

            Text(
                text = entry.message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}