package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.statusBarsHeight
import com.rerere.iwara4a.R
import com.rerere.iwara4a.util.openUrl
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@Composable
fun IndexDrawer(
    navController: NavController,
    indexViewModel: IndexViewModel,
    drawerState: DrawerState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    fun isLoading() = indexViewModel.loadingSelf

    val dialog = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialog,
        buttons = {
            positiveButton(stringResource(id = R.string.yes_button)) {
                dialog.hide()
                navController.navigate("login") {
                    popUpTo("index") {
                        inclusive = true
                    }
                }
            }
            negativeButton(stringResource(id = R.string.cancel_button)) {
                dialog.hide()
            }
        }
    ) {
        title(stringResource(id = R.string.screen_index_drawer_logout_title))
        message(stringResource(id = R.string.screen_index_drawer_logout_message))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Profile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsHeight(185.dp),
            color = MaterialTheme.colorScheme.background
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
                            .combinedClickable(
                                onLongClick = {
                                    dialog.show()
                                },
                                onClick = {
                                    navController.navigate("self")
                                }
                            )
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
                        text = if (isLoading()) stringResource(id = R.string.loading) else indexViewModel.self.nickname,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    // Email
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (indexViewModel.self.about != null) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = indexViewModel.self.about.let {
                                    if (it!!.isNotBlank()) it else stringResource(
                                        id = R.string.screen_index_drawer_self_about_empty
                                    )
                                }
                            )
                        } else {
                            Text(modifier = Modifier.weight(1f), text = indexViewModel.email)
                        }
                        IconButton(
                            modifier = Modifier.size(25.dp),
                            onClick = { indexViewModel.refreshSelf() }) {
                            Icon(
                                modifier = Modifier.size(25.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(id = R.string.screen_index_drawer_update_profile)
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
                .verticalScroll(rememberScrollState())
        ) {
            // 历史记录
            NavigationDrawerItem(
                selected = false,
                icon = {
                    Icon(Icons.Rounded.People, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_friends))
                },
                badge = {
                    AnimatedVisibility(visible = indexViewModel.self.friendRequest > 0) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(vertical = 4.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = indexViewModel.self.friendRequest.toString())
                        }
                    }
                },
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("friends")
                    }
                }
            )

            // 历史记录
            NavigationDrawerItem(
                selected = false,
                icon = {
                    Icon(Icons.Rounded.History, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_history))
                },
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("history")
                    }
                },
                badge = {}
            )

            // 下载
            NavigationDrawerItem(
                selected = false,
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("download")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Download, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_downloads))
                },
                badge = {}
            )

            // 喜欢
            NavigationDrawerItem(
                selected = false,
                icon = {
                    Icon(Icons.Rounded.Favorite, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_likes))
                },
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("like")
                    }
                },
                badge = {}
            )

            // 关注
            NavigationDrawerItem(
                selected = false,
                icon = {
                    Icon(Icons.Rounded.SupervisedUserCircle, null)
                },
                label = {
                    Text(stringResource(R.string.screen_follow_title))
                },
                badge = {},
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("following")
                    }
                }
            )

            // 播单
            NavigationDrawerItem(
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("playlist")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.PlaylistPlay, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_playlist))
                },
                badge = {},
                selected = false
            )

            // 论坛
            NavigationDrawerItem(
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("forum")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Forum, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_forum))
                },
                badge = {},
                selected = false
            )

            // 聊天室
            NavigationDrawerItem(
                onClick = {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("chat")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.ChatBubble, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_chat))
                },
                selected = false,
                badge = {}
            )

            // 设置
            NavigationDrawerItem(
                onClick =  {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("setting")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Settings, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_setting))
                },
                badge = {},
                selected = false
            )

            // 交流群
            var showDialog by remember { mutableStateOf(false) }
            if(showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text("交流群 | Chat Group")
                    },
                    text = {
                        Text("请选择加入交流群的类型:")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            coroutineScope.launch {
                                drawerState.close()
                                context.openUrl("https://t.me/iwara4a")
                            }
                        }) {
                            Text("Telegram")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                coroutineScope.launch {
                                    drawerState.close()
                                    context.openUrl("https://discord.gg/ceqzvbF2u9")
                                }
                            }
                        ){
                            Text("Discord")
                        }
                    }
                )
            }
            NavigationDrawerItem(
                onClick =  {
                    showDialog = true
                },
                icon = {
                    Icon(Icons.Rounded.Send, null)
                },
                label = {
                    Text(text = "群组")
                },
                badge = {},
                selected = false
            )
        }
    }
}