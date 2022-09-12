package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.md.Banner
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.local.LocalSelfData
import com.rerere.iwara4a.ui.screen.index.page.ExplorePage
import com.rerere.iwara4a.ui.screen.index.page.RankPage
import com.rerere.iwara4a.ui.screen.index.page.RecommendPage
import com.rerere.iwara4a.ui.screen.index.page.SubPage
import com.rerere.iwara4a.ui.states.rememberPrimaryClipboardState
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.findActivity
import com.rerere.iwara4a.util.getVersionName
import com.rerere.iwara4a.util.openUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.days

@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    //val screenType = rememberWindowSizeClass()
    val screenType = calculateWindowSizeClass(LocalContext.current.findActivity())
    BroadcastMessageDialog(indexViewModel)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                IndexDrawer(navController, indexViewModel, drawerState)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(drawerState, indexViewModel)
            },
            bottomBar = {
                BottomBar(
                    currentPage = pagerState.currentPage,
                    scrollToPage = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(it)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                val update by indexViewModel.updateChecker.collectAsState()
                var dismissUpdate by rememberSaveable {
                    mutableStateOf(false)
                }
                val context = LocalContext.current
                val currentVersion = LocalContext.current.getVersionName()
                AnimatedVisibility(
                    visible = update is DataState.Success
                            && update.readSafely()?.name != null
                            && update.readSafely()?.name != currentVersion
                            && !dismissUpdate
                ) {
                    Banner(
                        modifier = Modifier.padding(16.dp),
                        icon = {
                            Icon(Icons.Outlined.Update, null)
                        },
                        title = {
                            Text(text = "${stringResource(id = R.string.screen_index_update_title)}: ${update.readSafely()?.name}")
                        },
                        text = {
                            Text(text = update.readSafely()?.body ?: "", maxLines = 6)
                        },
                        buttons = {
                            TextButton(onClick = {
                                dismissUpdate = true
                            }) {
                                Text(text = stringResource(id = R.string.screen_index_button_update_neglect))
                            }
                            TextButton(onClick = {
                                context.openUrl("https://github.com/re-ovo/iwara4a/releases/latest")
                            }) {
                                Text(stringResource(id = R.string.screen_index_button_update_github))
                            }
                        }
                    )
                }
                ClipboardBanner()
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    count = 4,
                    userScrollEnabled = false
                ) { page ->
                    when (page) {
                        0 -> {
                            SubPage(indexViewModel)
                        }
                        1 -> {
                            RecommendPage(indexViewModel)
                        }
                        2 -> {
                            RankPage(indexViewModel)
                        }
                        3 -> {
                            ExplorePage(indexViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BroadcastMessageDialog(indexViewModel: IndexViewModel) {
    var dismissDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (!dismissDialog && indexViewModel.broadcastMessage.value.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { dismissDialog = true },
            confirmButton = {
                TextButton(onClick = { dismissDialog = true }) {
                    Text(stringResource(R.string.confirm_button))
                }
            },
            title = {
                Text("公告")
            },
            icon = {
                Icon(Icons.Outlined.BroadcastOnPersonal, null)
            },
            text = {
                Column {
                    indexViewModel.broadcastMessage.value.forEach {
                        Text(it)
                    }
                }
            }
        )
    }
}

@Composable
fun ClipboardBanner() {
    val navController = LocalNavController.current
    var clipBoard by rememberPrimaryClipboardState()
    var showDialog by remember {
        mutableStateOf(false)
    }
    var link by remember {
        mutableStateOf("")
    }
    LaunchedEffect(clipBoard) {
        clipBoard?.getItemAt(0)?.let {
            if (it.text?.matches(Regex("https://ecchi.iwara.tv/videos/.+")) == true) {
                link = it.text.toString().substringAfter("/videos/")
                showDialog = true
            } else {
                showDialog = false
            }
        } ?: kotlin.run {
            showDialog = false
        }
    }
    AnimatedVisibility(showDialog) {
        Banner(
            modifier = Modifier.padding(16.dp),
            title = {
                Text("检测到剪贴板存在视频链接")
            },
            text = {
                Text("https://ecchi.iwara.tv/videos/$link")
            },
            icon = {
                Icon(Icons.Outlined.Link, null)
            },
            buttons = {
                TextButton(
                    onClick = {
                        clipBoard = null
                    }
                ) {
                    Text("移除")
                }
                TextButton(
                    onClick = {
                        navController.navigate("video/$link")
                    }
                ) {
                    Text("打开")
                }
            }
        )
    }
}

@Composable
private fun TopBar(
    drawerState: DrawerState,
    indexViewModel: IndexViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val context = LocalContext.current
    var donationDialog by remember {
        mutableStateOf(false)
    }
    if (donationDialog) {
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
    val self = LocalSelfData.current
    LaunchedEffect(Unit) {
        delay(100)
        val setting = context.sharedPreferencesOf("donation")
        if (
            System.currentTimeMillis() - setting.getLong(
                "lastPopup",
                0L
            ) >= 1.days.inWholeMilliseconds
            && (self.numId <= 195_0000 || !self.profilePic.contains("default-avatar.png"))
            && Locale.getDefault().language == Locale.SIMPLIFIED_CHINESE.language
        ) {
            donationDialog = true
            setting.edit {
                putLong("lastPopup", System.currentTimeMillis())
            }
        }
    }
    Md3TopBar(
        title = {
            Text(
                text = if (indexViewModel.loadingSelf) {
                    stringResource(R.string.app_name)
                } else {
                    indexViewModel.self.nickname
                },
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch {
                    drawerState.open()
                }
            }) {
                AsyncImage(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    model = indexViewModel.self.profilePic,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    navController.navigate("message")
                }
            ) {
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(visible = indexViewModel.self.messages > 0) {
                            Badge {
                                Text(text = indexViewModel.self.messages.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Message, null)
                }
            }

            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Outlined.Search, null)
            }

            if (BuildConfig.DEBUG) {
                IconButton(
                    onClick = {
                        navController.navigate("test")
                    }
                ) {
                    Icon(Icons.Outlined.Work, null)
                }
            }
        }
    )
}

@Composable
private fun SideRail(currentPage: Int, scrollToPage: (Int) -> Unit) {
    NavigationRail {
        NavigationRailItem(
            selected = currentPage == 0,
            onClick = {
                scrollToPage(0)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Subscriptions, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sub))
            },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentPage == 1,
            onClick = {
                scrollToPage(1)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Recommend, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_recommend))
            },
            alwaysShowLabel = false
        )
        NavigationRailItem(
            selected = currentPage == 2,
            onClick = {
                scrollToPage(2)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sort))
            },
            alwaysShowLabel = false
        )

        NavigationRailItem(
            selected = currentPage == 3,
            onClick = {
                scrollToPage(3)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Explore, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_explore))
            },
            alwaysShowLabel = false
        )
    }
}

@Composable
private fun BottomBar(currentPage: Int, scrollToPage: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = currentPage == 0,
            onClick = {
                scrollToPage(0)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Subscriptions, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sub))
            },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            selected = currentPage == 1,
            onClick = {
                scrollToPage(1)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Recommend, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_recommend))
            },
            alwaysShowLabel = false
        )
        NavigationBarItem(
            selected = currentPage == 2,
            onClick = {
                scrollToPage(2)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Sort, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sort))
            },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = currentPage == 3,
            onClick = {
                scrollToPage(3)
            },
            icon = {
                Icon(imageVector = Icons.Outlined.Explore, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_explore))
            },
            alwaysShowLabel = false
        )
    }
}