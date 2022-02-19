package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ListItem
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
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
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.google.accompanist.insets.statusBarsHeight
import com.rerere.iwara4a.R
import com.rerere.iwara4a.repo.SelfId
import com.rerere.iwara4a.util.openUrl
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import java.util.*

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
        ) {
            // 历史记录
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("friends")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.People, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_friends))
                },
                trailing = {
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
                }
            )

            // 历史记录
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("history")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.History, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_history))
                }
            )

            // 下载
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("download")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Download, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_downloads))
                }
            )

            // 喜欢
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("like")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Favorite, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_likes))
                }
            )

            // 关注
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("following")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.SupervisedUserCircle, null)
                },
                text = {
                    Text(stringResource(R.string.screen_follow_title))
                }
            )

            // 播单
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("playlist")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.PlaylistPlay, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_playlist))
                }
            )

            // 论坛
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("forum")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Forum, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_forum))
                }
            )

            // 聊天室
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("chat")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.ChatBubble, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_chat))
                }
            )

            // Donation
            AnimatedVisibility(
                Locale.getDefault().country == Locale.CHINA.country && SelfId <= 190_0000
            ) {
                ListItem(
                    modifier = Modifier.clickable { context.openUrl("https://afdian.net/@re_ovo")  },
                    icon = {
                        Icon(Icons.Default.Money, null)
                    },
                    text = {
                        Text(text = "爱发电")
                    }
                )
            }

            // 设置
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate("setting")
                    }
                },
                icon = {
                    Icon(Icons.Rounded.Settings, null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_setting))
                }
            )

            // 交流群
            ListItem(
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        drawerState.close()
                        context.openUrl("https://discord.gg/ceqzvbF2u9")
                    }
                },
                icon = {
                    Icon(painterResource(R.drawable.outline_discord_20), null)
                },
                text = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_discord))
                }
            )
        }
    }
}