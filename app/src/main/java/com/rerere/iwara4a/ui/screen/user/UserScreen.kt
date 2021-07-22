package com.rerere.iwara4a.ui.screen.user

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.user.UserData
import com.rerere.iwara4a.ui.public.*
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun UserScreen(
    navController: NavController,
    userId: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        userViewModel.load(userId)
    }

    Scaffold(
        topBar = {
            TopBar(navController, userViewModel)
        },
        modifier = Modifier.navigationBarsPadding()
    ) {
        when {
            userViewModel.isLoaded() -> {
                UserInfo(navController, userViewModel.userData, userViewModel)
            }
            userViewModel.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = "加载中", fontWeight = FontWeight.Bold)
                    }
                }
            }
            userViewModel.error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable { userViewModel.load(userId) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .padding(10.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(R.drawable.anime_4),
                                contentDescription = null
                            )
                        }
                        Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun UserInfo(
    navController: NavController,
    userData: UserData,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    Column {
        // 用户信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberImagePainter(userData.pic),
                            contentDescription = null
                        )
                    }

                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = userData.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = PINK
                        )
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            Text(
                                text = "注册日期: ${userData.joinDate}"
                            )
                            Text(
                                text = "最后在线: ${userData.lastSeen}"
                            )
                        }
                    }

                    // 关注
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                userViewModel.handleFollow { action, success ->
                                    if (action) {
                                        Toast
                                            .makeText(
                                                context,
                                                if (success) "关注了该UP主！ ヾ(≧▽≦*)o" else "关注失败",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                if (success) "已取消关注" else "取消关注失败",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                            }
                            .background(
                                if (userData.follow) Color.LightGray else Color(
                                    0xfff45a8d
                                )
                            )
                            .padding(4.dp),
                    ) {
                        Text(
                            text = if (userData.follow) "已关注" else "+ 关注",
                            color = if (userData.follow) Color.Black else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = userData.about, maxLines = 5)
                }
            }
        }
        // 评论/ 视频 / 图片
        val pagerState = rememberPagerState(pageCount = 3)
        TabRow {
            TabItem(pagerState = pagerState, index = 0, text = "评论")
            TabItem(pagerState = pagerState, index = 1, text = "发布的视频")
            TabItem(pagerState = pagerState, index = 2, text = "发布的图片")
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), state = pagerState
        ) {
            when (it) {
                0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CommentList(navController, userViewModel)
                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        VideoList(navController, userViewModel)
                    }
                }
                2 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "还没做 🚗")
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController, userViewModel: UserViewModel) {
    FullScreenTopBar(
        title = {
            Text(text = if (userViewModel.isLoaded()) userViewModel.userData.username else "用户信息")
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        }
    )
}

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
private fun CommentList(navController: NavController, userViewModel: UserViewModel) {
    when {
        userViewModel.error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "用户信息加载失败")
            }
        }
        !userViewModel.isLoaded() -> {
            Column(Modifier.fillMaxSize()) {
                repeat(10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp)
                            .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
                    )
                }
            }
        }
        else -> {
            val videoList = userViewModel.commentPager.collectAsLazyPagingItems()
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = videoList.loadState.refresh == LoadState.Loading),
                onRefresh = {
                    videoList.refresh()
                }) {
                LazyColumn {
                    if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "个人空间无评论")
                            }
                        }
                    }

                    items(videoList) {
                        CommentItem(navController, it!!)
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun VideoList(navController: NavController, userViewModel: UserViewModel) {
    when {
        userViewModel.error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "用户信息加载失败")
            }
        }
        !userViewModel.isLoaded() -> {
            Column(Modifier.fillMaxSize()) {
                repeat(10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(16.dp)
                            .placeholder(visible = true, highlight = PlaceholderHighlight.shimmer())
                    )
                }
            }
        }
        else -> {
            val videoList = userViewModel.videoPager.collectAsLazyPagingItems()
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = videoList.loadState.refresh == LoadState.Loading),
                onRefresh = {
                    videoList.refresh()
                }) {
                if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "没有发布视频")
                    }
                } else {
                    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                        items(videoList) {
                            MediaPreviewCard(navController, it!!)
                        }
                    }
                }
            }
        }
    }
}