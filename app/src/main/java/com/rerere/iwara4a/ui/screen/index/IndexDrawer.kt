package com.rerere.iwara4a.ui.screen.index

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
                            if(indexViewModel.self.about != null){
                                Text(modifier = Modifier.weight(1f), text = indexViewModel.self.about!!)
                            }else {
                                Text(modifier = Modifier.weight(1f), text = indexViewModel.email)
                            }
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
            // 下载
            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("download")
                },
                icon = {
                    Icon(Icons.Default.FileDownload, null)
                },
                text = {
                    Text(text = "缓存")
                }
            )

            // 喜欢
            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("like")
                },
                icon = {
                    Icon(Icons.Default.Favorite, null)
                },
                text = {
                    Text(text = "喜欢")
                }
            )

            // 播单
            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("playlist")
                },
                icon = {
                    Icon(Icons.Default.FeaturedPlayList, null)
                },
                text = {
                    Text(text = "播单")
                }
            )

            // 设置
            ListItem(
                modifier = Modifier.clickable {
                    navController.navigate("setting")
                },
                icon = {
                    Icon(Icons.Default.Settings, null)
                },
                text = {
                    Text(text = "设置")
                }
            )

            // 关于
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