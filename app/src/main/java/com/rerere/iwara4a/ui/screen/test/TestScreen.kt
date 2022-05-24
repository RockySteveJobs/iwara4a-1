package com.rerere.iwara4a.ui.screen.test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.rerere.iwara4a.service.DownloadService
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.util.memSaver

@Composable
fun TestScreen() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            SimpleIwaraTopBar("Test")
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding
        ) {
            item {
                Button(
                    onClick = {
                        context.bindService(
                            Intent(context, DownloadService::class.java),
                            object : ServiceConnection {
                                override fun onServiceConnected(
                                    name: ComponentName?,
                                    service: IBinder?
                                ) {
                                    println("Service connected: ${name?.className}")
                                    println("Binder: ${service?.javaClass?.name}")
                                    val downloadBinder = service as DownloadService.DownloadBinder
                                    val downloadService = downloadBinder.service

                                    println(downloadService)
                                }

                                override fun onServiceDisconnected(name: ComponentName?) {
                                    println("Service disconnected!")
                                }
                            },
                            Context.BIND_AUTO_CREATE
                        )
                    }
                ) {
                    Text("Bind service")
                }
            }

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