package com.rerere.iwara4a.ui.screen.log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.util.plus
import com.rerere.iwara4a.util.setClipboard
import java.io.File

@Composable
fun LoggerScreen() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val logContent by produceState(initialValue = "") {
        File(context.filesDir, "log")
            .takeIf { it.exists() }
            ?.let {
                val file = File(
                    it,
                    DateFileNameGenerator().generateFileName(
                        LogLevel.ALL,
                        System.currentTimeMillis()
                    )
                )
                file.takeIf { file.exists() }
                    ?.let {
                        val content = file.readText()
                        value = content
                    }
            } ?: kotlin.run {
            value = "日志文件夹不存在！"
        }
    }
    Scaffold(topBar = {
        Md3TopBar(
            title = {
                Text(text = "APP日志")
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            },
            actions = {
                IconButton(onClick = {
                    context.setClipboard(logContent)
                }) {
                    Icon(Icons.Default.ContentCopy, null)
                }
            }
        )
    }) {
        val logLines = logContent.split("\n").filter { it.isNotEmpty() }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = WindowInsets.navigationBars.asPaddingValues() + PaddingValues(
                horizontal = 8.dp
            )
        ) {
            items(logLines){
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    SelectionContainer {
                        Text(text = it, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}