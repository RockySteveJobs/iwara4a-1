package com.rerere.iwara4a.ui.screen.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.screen.index.page.IRCPage
import com.rerere.iwara4a.ui.screen.index.page.ImageListPage
import com.rerere.iwara4a.ui.screen.index.page.SubPage
import com.rerere.iwara4a.ui.screen.index.page.VideoListPage
import com.rerere.iwara4a.ui.theme.uiBackGroundColor
import com.rerere.iwara4a.util.currentVisualPage
import com.rerere.iwara4a.util.getVersionName
import com.rerere.iwara4a.util.openUrl
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun IndexScreen(navController: NavController, indexViewModel: IndexViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = 4, initialPage = 0, initialOffscreenLimit = 1)
    val scaffoldState = rememberScaffoldState()

    // 更新提醒
    val updateDialog = remember {
        MaterialDialog()
    }
    val update by indexViewModel.updateChecker.observeAsState(initial = Response.failed())
    updateDialog.build(
        buttons = {
            button("前往Github更新") {
                updateDialog.hide()
                context.openUrl("https://github.com/jiangdashao/iwara4a/releases/latest")
            }
            button("忽略") {
                updateDialog.hide()
            }
        }
    ) {
        if (update.isSuccess()) {
            title("APP有更新: ${update.read().name}")
            message("更新内容:\n${update.read().body}")
            message("(加了QQ群的也可以在群文件下载更新)")
        }
    }

    // 捐助提醒
    val dialog = remember {
        MaterialDialog()
    }
    dialog.build(
        buttons = {
            positiveButton("好的") {
                dialog.hide()
                navController.navigate("donate")
            }

            negativeButton("不了") {
                dialog.hide()
            }
        }
    ) {
        title("捐助作者")
        message("开发APP不容易，考虑捐助一下吗？")
    }

    LaunchedEffect(Unit) {
        sharedPreferencesOf("donate").let {
            val lastShow = it.getLong("lastshow", 0L)
            if (System.currentTimeMillis() - lastShow >= 24 * 3600 * 1000L) {
                dialog.show()
                it.edit {
                    putLong("lastshow", System.currentTimeMillis())
                }
            } else {
                println("还未到展示捐助对话框的时间")
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            val coroutineScope = rememberCoroutineScope()
            FullScreenTopBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        val painter = rememberImagePainter(indexViewModel.self.profilePic)
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .placeholder(
                                    visible = painter.state is ImagePainter.State.Loading,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painter,
                                contentDescription = null
                            )
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(visible = update.isSuccess() && update.read().name != context.getVersionName()) {
                        IconButton(onClick = {
                            updateDialog.show()
                        }) {
                            Icon(Icons.Default.Update, null)
                        }
                    }
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, null)
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(pagerState = pagerState)
        },
        drawerContent = {
            IndexDrawer(navController, indexViewModel)
        }
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            state = pagerState
        ) { page ->
            when (page) {
                0 -> {
                    SubPage(navController, indexViewModel)
                }
                1 -> {
                    VideoListPage(navController, indexViewModel)
                }
                2 -> {
                    ImageListPage(navController, indexViewModel)
                }
                3 -> {
                    IRCPage(navController, indexViewModel)
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun BottomBar(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    BottomNavigation(
        modifier = Modifier.navigationBarsWithImePadding(),
        backgroundColor = MaterialTheme.colors.uiBackGroundColor
    ) {
        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 0,
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(
                        page = 0
                    )
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null)
            },
            label = {
                Text(text = "关注")
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        )

        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 1,
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(
                        page = 1
                    )
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.FeaturedVideo, contentDescription = null)
            },
            label = {
                Text(text = "视频")
            }, selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        )

        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 2,
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(
                        page = 2
                    )
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.Image, contentDescription = null)
            },
            label = {
                Text(text = "图片")
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        )

        BottomNavigationItem(
            selected = pagerState.currentVisualPage == 3,
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(
                        page = 3
                    )
                }
            },
            icon = {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null)
            },
            label = {
                Text(text = "聊天")
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        )
    }
}