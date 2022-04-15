package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.rerere.iwara4a.repo.SelfId
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.component.AppBarStyle
import com.rerere.iwara4a.ui.component.Md3BottomNavigation
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.md.Banner
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.local.LocalSelfData
import com.rerere.iwara4a.ui.screen.index.page.ExplorePage
import com.rerere.iwara4a.ui.screen.index.page.RankPage
import com.rerere.iwara4a.ui.screen.index.page.SubPage
import com.rerere.iwara4a.ui.states.WindowSize
import com.rerere.iwara4a.ui.states.rememberWindowSizeClass
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.getVersionName
import com.rerere.iwara4a.util.openUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.Duration.Companion.days

@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(0)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
    val screenType = rememberWindowSizeClass()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            IndexDrawer(navController, indexViewModel, drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(drawerState, indexViewModel, scrollBehavior)
            },
            bottomBar = {
                AnimatedVisibility(screenType == WindowSize.Compact) {
                    BottomBar(
                        currentPage = pagerState.currentPage,
                        scrollToPage = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(it)
                            }
                        }
                    )
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                AnimatedVisibility(screenType > WindowSize.Compact) {
                    SideRail(pagerState.currentPage) {
                        coroutineScope.launch { pagerState.scrollToPage(it) }
                    }
                }
                Column {
                    val update by indexViewModel.updateChecker.collectAsState()
                    var dismissUpdate by rememberSaveable {
                        mutableStateOf(false)
                    }
                    val context = LocalContext.current
                    val currentVersion = LocalContext.current.getVersionName()
                    AnimatedVisibility(
                        visible = update is DataState.Success && update.read().name != currentVersion && !dismissUpdate
                    ) {
                        Banner(
                            modifier = Modifier.padding(16.dp),
                            icon = {
                                Icon(Icons.Rounded.Update, null)
                            },
                            title = {
                                Text(text = "${stringResource(id = R.string.screen_index_update_title)}: ${update.read().name}")
                            },
                            text = {
                                Text(text = update.read().body, maxLines = 6)
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
                                RankPage(indexViewModel)
                            }
                            2 -> {
                                ExplorePage(indexViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    drawerState: DrawerState,
    indexViewModel: IndexViewModel,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val coroutineScope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val context = LocalContext.current
    var donationDialog by remember {
        mutableStateOf(false)
    }
    if (donationDialog) {
        AlertDialog(
            onDismissRequest = { donationDialog = false },
            confirmButton = {
                TextButton(onClick = { context.openUrl("https://afdian.net/@re_ovo") }) {
                    Text(text = "我想捐助")
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
                Text(text = "你的赞助可以给我更多动力来更新更多功能, 感谢你对app的支持")
            }
        )
    }
    val self = LocalSelfData.current
    LaunchedEffect(indexViewModel) {
        delay(100)
        val setting = sharedPreferencesOf("donation")
        if (
            System.currentTimeMillis() - setting.getLong(
                "lastPopup",
                0L
            ) >= 1.days.inWholeMilliseconds
            && self.numId <= 190_0000
            && Locale.getDefault().language == Locale.SIMPLIFIED_CHINESE.language
        ) {
            donationDialog = true
            setting.edit {
                putLong("lastPopup", System.currentTimeMillis())
            }
        }
    }
    Md3TopBar(
        appBarStyle = AppBarStyle.Small,
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = if(indexViewModel.loadingSelf) {
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
            IconButton(onClick = {
                navController.navigate("message")
            }) {
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(visible = indexViewModel.self.messages > 0) {
                            Badge {
                                Text(text = indexViewModel.self.messages.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Rounded.Message, null)
                }
            }

            IconButton(onClick = { navController.navigate("search") }) {
                Icon(Icons.Rounded.Search, null)
            }

            if(BuildConfig.DEBUG){
                IconButton(onClick = { navController.navigate("test") }) {
                    Icon(Icons.Rounded.Work, null)
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
                Icon(imageVector = Icons.Rounded.Subscriptions, contentDescription = null)
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
                Icon(imageVector = Icons.Rounded.Sort, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sort))
            },
            alwaysShowLabel = false
        )

        NavigationRailItem(
            selected = currentPage == 2,
            onClick = {
                scrollToPage(2)
            },
            icon = {
                Icon(imageVector = Icons.Rounded.Explore, contentDescription = null)
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
    Md3BottomNavigation {
        NavigationBarItem(
            selected = currentPage == 0,
            onClick = {
                scrollToPage(0)
            },
            icon = {
                Icon(imageVector = Icons.Rounded.Subscriptions, contentDescription = null)
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
                Icon(imageVector = Icons.Rounded.Sort, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_sort))
            },
            alwaysShowLabel = false
        )

        NavigationBarItem(
            selected = currentPage == 2,
            onClick = {
                scrollToPage(2)
            },
            icon = {
                Icon(imageVector = Icons.Rounded.Explore, contentDescription = null)
            },
            label = {
                Text(text = stringResource(R.string.screen_index_bottom_explore))
            },
            alwaysShowLabel = false
        )
    }
}