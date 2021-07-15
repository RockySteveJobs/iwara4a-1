package com.rerere.iwara4a.ui.screen.index

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.statusBarsHeight

@ExperimentalMaterialApi
@Composable
fun IndexDrawer(navController: NavController, indexViewModel: IndexViewModel) {
    val context = LocalContext.current
    fun isLoading() = indexViewModel.loadingSelf
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Profile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight(185.dp),
            elevation = 4.dp,
            color = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Profile Pic
                Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                navController.navigate("login")
                            }
                    ) {
                        val painter = rememberImagePainter(indexViewModel.self.profilePic)
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painter,
                            contentDescription = null
                        )
                    }
                }

                // Profile Info
                Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)) {
                    // UserName
                    Text(
                        text = if (isLoading()) "加载中" else indexViewModel.self.nickname,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                    // Email
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(modifier = Modifier.weight(1f), text = indexViewModel.email)
                        }
                        IconButton(
                            modifier = Modifier.size(25.dp),
                            onClick = { indexViewModel.refreshSelf() }) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "刷新个人信息"
                            )
                        }
                    }
                }
            }
        }

        // Navigation List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            ListItem(
                modifier = Modifier.clickable {
                    Toast.makeText(context, "暂未实现", Toast.LENGTH_SHORT).show()
                },
                icon = {
                    Icon(Icons.Default.FileDownload, null)
                },
                text = {
                    Text(text = "缓存")
                }
            )

            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("like")
                },
                icon = {
                    Icon(Icons.Default.Subscriptions, null)
                },
                text = {
                    Text(text = "喜欢的视频")
                }
            )

            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("playlist")
                },
                icon = {
                    Icon(Icons.Default.Subscriptions, null)
                },
                text = {
                    Text(text = "播单")
                }
            )

            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("about")
                },
                icon = {
                    Icon(Icons.Default.Info, null)
                },
                text = {
                    Text(text = "关于")
                }
            )
        }
    }
}