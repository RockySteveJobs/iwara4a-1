package com.rerere.iwara4a.ui.screen.user

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
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
import com.rerere.iwara4a.ui.component.*
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.launch

@Composable
fun UserScreen(
    navController: NavController,
    userId: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        userViewModel.load(userId)
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            decayAnimationSpec
        )
    }

    Scaffold(
        topBar = {
            TopBar(scrollBehavior, userViewModel)
        }
    ) {
        when {
            userViewModel.isLoaded() -> {
                Column(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
                    UserDescription(
                        userData = userViewModel.userData,
                        userViewModel = userViewModel
                    )
                    UserInfo(navController, userViewModel.userData, userViewModel)
                }
            }
            userViewModel.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(
                            R.raw.loading_circles
                        )
                    )
                    LottieAnimation(
                        modifier = Modifier.size(250.dp),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
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
                        Text(
                            text = stringResource(id = R.string.load_error),
                            fontWeight = FontWeight.Bold
                        )
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 头像
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(60.dp),
                    model = userData.pic,
                    contentDescription = null
                )

                // 用户信息
                Column(Modifier.padding(horizontal = 16.dp)) {
                    // 用户名
                    Text(
                        text = userData.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PINK
                    )

                    ProvideTextStyle(TextStyle(fontSize = 14.sp)) {
                        // 加入日期
                        Text(
                            text = "${stringResource(id = R.string.screen_user_description_join_date)}: ${userData.joinDate}"
                        )

                        // 最后活跃日期
                        Text(
                            text = "${stringResource(id = R.string.screen_user_description_last_seen)}: ${userData.lastSeen}"
                        )
                    }
                }
            }

            // 用户简介
            var expand by remember {
                mutableStateOf(false)
            }
            Text(
                text = userData.about.ifBlank { stringResource(id = R.string.screen_user_description_lazy) },
                maxLines = if (expand) 10 else 3,
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    expand = !expand
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            // 关注
            Button(
                onClick = {
                    userViewModel.handleFollow { action, success ->
                        if (action) {
                            Toast
                                .makeText(
                                    context,
                                    if (success) "${context.stringResource(id = R.string.follow_success)} ヾ(≧▽≦*)o" else context.stringResource(
                                        id = R.string.follow_fail
                                    ),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        } else {
                            Toast
                                .makeText(
                                    context,
                                    if (success) context.stringResource(id = R.string.unfollow_success) else context.stringResource(
                                        id = R.string.unfollow_fail
                                    ),
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                }
            ) {
                Text(
                    text = if (userData.follow) stringResource(id = R.string.follow_status_following) else "+ ${
                        stringResource(
                            id = R.string.follow_status_not_following
                        )
                    }"
                )
            }
            // 好友
            Button(
                onClick = {
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
            ) {
                Text(
                    text = when (userData.friend) {
                        UserFriendState.NOT -> stringResource(id = R.string.screen_user_description_friend_state_add)
                        UserFriendState.PENDING -> stringResource(id = R.string.screen_user_description_friend_state_pending)
                        UserFriendState.ALREADY -> stringResource(id = R.string.screen_user_description_friend_state_already)
                    },
                    maxLines = 1
                )
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
    val coroutineScope = rememberCoroutineScope()
    Column {
        // 评论/ 视频 / 图片
        val pagerState = rememberPagerState(0)
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, it))
            }
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
private fun TopBar(scrollBehavior: TopAppBarScrollBehavior, userViewModel: UserViewModel) {
    Md3TopBar(
        title = {
            Text(
                text = if (userViewModel.isLoaded()) {
                    userViewModel.userData.username
                } else {
                    stringResource(
                        id = R.string.screen_user_topbar_title
                    )
                }
            )
        },
        navigationIcon = {
            BackIcon()
        },
        scrollBehavior = scrollBehavior,
        appBarStyle = AppBarStyle.Small
    )
}

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
                    SwipeRefreshIndicator(
                        s,
                        trigger,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
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
                        CommentItem(navController, it!!) {
                            // TODO: 实现个人主页的回复功能
                        }
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
                    SwipeRefreshIndicator(
                        s,
                        trigger,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(id = R.string.screen_user_video_nothing))
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
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
                    SwipeRefreshIndicator(
                        s,
                        trigger,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                if (videoList.loadState.refresh is LoadState.NotLoading && videoList.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(id = R.string.screen_user_image_nothing))
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                        items(videoList) {
                            MediaPreviewCard(navController, it!!)
                        }
                    }
                }
            }
        }
    }
}