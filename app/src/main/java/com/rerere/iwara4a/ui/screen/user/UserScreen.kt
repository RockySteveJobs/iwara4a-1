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
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.user.UserData
import com.rerere.iwara4a.model.user.UserFriendState
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.CommentItem
import com.rerere.iwara4a.ui.public.IwaraTopBar
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.ui.public.items
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.noRippleClickable
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.launch

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
                Column {
                    UserDescription(
                        userData = userViewModel.userData,
                        userViewModel = userViewModel
                    )
                    UserInfo(navController, userViewModel.userData, userViewModel)
                }
            }
            userViewModel.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = stringResource(id = R.string.loading), fontWeight = FontWeight.Bold)
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
                        Text(text = stringResource(id = R.string.load_error), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserDescription(userData: UserData, userViewModel: UserViewModel) {
    val navController = LocalNavController.current
    val context = LocalContext.current
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
                            text = "${stringResource(id = R.string.screen_user_description_join_date)}: ${userData.joinDate}"
                        )
                        Text(
                            text = "${stringResource(id = R.string.screen_user_description_last_seen)}: ${userData.lastSeen}"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = userData.about.let {
                    it.ifBlank { stringResource(id = R.string.screen_user_description_lazy) }
                }, maxLines = 5)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 关注
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .weight(2f)
                .clickable {
                    userViewModel.handleFollow { action, success ->
                        if (action) {
                            Toast
                                .makeText(
                                    context,
                                    if (success) "${context.stringResource(id = R.string.follow_success)} ヾ(≧▽≦*)o" else context.stringResource(id = R.string.follow_fail),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    if (success) context.stringResource(id = R.string.unfollow_success) else context.stringResource(id = R.string.unfollow_fail),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                }
                .background(
                    if (userData.follow) Color.LightGray else MaterialTheme.colors.primary
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (userData.follow) stringResource(id = R.string.follow_status_following) else "+ ${stringResource(id = R.string.follow_status_not_following)}",
                color = if (userData.follow) Color.Black else Color.White
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        // 好友
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .weight(1f)
                .clickable {
                    when (userData.friend) {
                        UserFriendState.NOT -> {
                            userViewModel.handleFriendRequest {
                                userViewModel.load(userId = userData.userId)
                            }
                        }
                        UserFriendState.ALREADY -> {
                            Toast
                                .makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_user_description_friend_unregister),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            navController.navigate("friends")
                        }
                        else -> {
                        }
                    }
                }
                .background(
                    when (userData.friend) {
                        UserFriendState.NOT -> MaterialTheme.colors.primary
                        UserFriendState.PENDING -> MaterialTheme.colors.secondary
                        UserFriendState.ALREADY -> Color.LightGray
                    }
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (userData.friend) {
                    UserFriendState.NOT -> stringResource(id = R.string.screen_user_description_friend_state_add)
                    UserFriendState.PENDING -> stringResource(id = R.string.screen_user_description_friend_state_pending)
                    UserFriendState.ALREADY -> stringResource(id = R.string.screen_user_description_friend_state_already)
                },
                color = if (userData.friend == UserFriendState.ALREADY) Color.Black else Color.White,
                maxLines = 1
            )
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
    val coroutineScope = rememberCoroutineScope()
    Column {
        // 评论/ 视频 / 图片
        val pagerState = rememberPagerState(0)
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = MaterialTheme.colors.primary
                )
            },
            backgroundColor = MaterialTheme.colors.background
        ) {
            Tab(
                text = { Text(stringResource(id = R.string.screen_user_info_message)) },
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
            )
            Tab(
                text = { Text(stringResource(id = R.string.screen_user_info_video)) },
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
            )
            Tab(
                text = { Text(stringResource(id = R.string.screen_user_info_image)) },
                selected = pagerState.currentPage == 2,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                },
            )
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState,
            count = 3
        ) {
            when (it) {
                0 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CommentList(navController, userViewModel)
                    }
                }
                1 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        VideoList(navController, userViewModel)
                    }
                }
                2 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ImageList(navController, userViewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController, userViewModel: UserViewModel) {
    IwaraTopBar(
        title = {
            Text(text = if (userViewModel.isLoaded()) userViewModel.userData.username else stringResource(id = R.string.screen_user_topbar_title))
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
                Text(text = stringResource(id = R.string.screen_user_load_fail))
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
                },
                indicator = { s, trigger ->
                    SwipeRefreshIndicator(s, trigger, contentColor = MaterialTheme.colors.primary)
                }
            ) {
                LazyColumn {
                    if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = stringResource(id = R.string.screen_user_comment_empty))
                            }
                        }
                    }

                    items(videoList) {
                        CommentItem(navController, it!!, {
                            // TODO: 实现个人主页的回复功能
                        })
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
                Text(text = stringResource(id = R.string.screen_user_load_fail))
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
                },
                indicator = { s, trigger ->
                    SwipeRefreshIndicator(s, trigger, contentColor = MaterialTheme.colors.primary)
                }
            ) {
                if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(id = R.string.screen_user_video_nothing))
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

@ExperimentalFoundationApi
@Composable
private fun ImageList(navController: NavController, userViewModel: UserViewModel) {
    when {
        userViewModel.error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.screen_user_load_fail))
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
            val videoList = userViewModel.imagePager.collectAsLazyPagingItems()
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = videoList.loadState.refresh == LoadState.Loading),
                onRefresh = {
                    videoList.refresh()
                },
                indicator = { s, trigger ->
                    SwipeRefreshIndicator(s, trigger, contentColor = MaterialTheme.colors.primary)
                }
            ) {
                if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(id = R.string.screen_user_image_nothing))
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