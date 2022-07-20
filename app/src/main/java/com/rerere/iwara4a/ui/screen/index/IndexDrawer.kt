package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rerere.iwara4a.R
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.local.LocalSelfData
import com.rerere.iwara4a.util.openUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.days

@Composable
fun IndexDrawer(
    navController: NavController,
    indexViewModel: IndexViewModel,
    drawerState: DrawerState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    fun isLoading() = indexViewModel.loadingSelf

    var dialog by remember {
        mutableStateOf(false)
    }
    if(dialog){
        AlertDialog(
            onDismissRequest = { dialog = false },
            title = {
                Text(stringResource(id = R.string.screen_index_drawer_logout_title))
            },
            text = {
                Text(stringResource(id = R.string.screen_index_drawer_logout_message))
            },
            confirmButton = {
                TextButton(onClick = {
                    dialog = false
                    navController.navigate("login") {
                        popUpTo("index") {
                            inclusive = true
                        }
                    }
                }) {
                    Text(stringResource(R.string.yes_button))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Profile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 18.dp),
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
                                    dialog = true
                                },
                                onClick = {
                                    navController.navigate("self")
                                }
                            )
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = indexViewModel.self.profilePic,
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
                                imageVector = Icons.Outlined.Refresh,
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
                    Icon(Icons.Outlined.People, null)
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
                    Icon(Icons.Outlined.History, null)
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
                    Icon(Icons.Outlined.Download, null)
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
                    Icon(Icons.Outlined.Favorite, null)
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
                    Icon(Icons.Outlined.SupervisedUserCircle, null)
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
                    Icon(Icons.Outlined.PlaylistPlay, null)
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
                    Icon(Icons.Outlined.Forum, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_forum))
                },
                badge = {},
                selected = false
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
                    Icon(Icons.Outlined.Settings, null)
                },
                label = {
                    Text(text = stringResource(R.string.screen_index_drawer_item_setting))
                },
                badge = {},
                selected = false
            )

            // 交流群
            var donationDialog by remember { mutableStateOf(false) }
            var shouldDisplayDonation by remember {
                mutableStateOf(false)
            }
            if(donationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        donationDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                context.openUrl("https://qr.alipay.com/fkx16008rhxy0oewbc6vra8")
                            }
                        ) {
                            Text("支付宝")
                        }
                        TextButton(
                            onClick = {
                                context.openUrl("https://afdian.net/@re_ovo")
                            }
                        ) {
                            Text(text = "爱发电")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { donationDialog = false }) {
                            Text(text = "不了")
                        }
                    },
                    title = {
                        Text(text = "考虑赞助一下作者吗?")
                    },
                    text = {
                        Text(text = "你的赞助可以给我更多动力来更新更多功能, 同时为app的推荐爬虫服务支付费用，感谢你对app的支持")
                    },
                    icon = {
                        Icon(Icons.Outlined.Payment, null)
                    }
                )
            }
            if(shouldDisplayDonation) {
                NavigationDrawerItem(
                    onClick = {
                        donationDialog = true
                    },
                    icon = {
                        Icon(Icons.Outlined.Payments, null)
                    },
                    label = {
                        Text(text = "赞助")
                    },
                    selected = false
                )
            }
            val self = LocalSelfData.current
            LaunchedEffect(Unit) {
                delay(100)
                if (
                    (self.numId <= 195_0000 || !self.profilePic.contains("default-avatar.png"))
                    && Locale.getDefault().language == Locale.SIMPLIFIED_CHINESE.language
                ) {
                    shouldDisplayDonation = true
                }
            }
        }
    }
}