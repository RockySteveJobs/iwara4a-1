package com.rerere.iwara4a.ui.screen.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.ui.local.LocalScreenOrientation
import com.rerere.iwara4a.ui.public.*
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.ui.theme.uiBackGroundColor
import com.rerere.iwara4a.util.noRippleClickable
import com.rerere.iwara4a.util.shareMedia
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.input
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@SuppressLint("WrongConstant")
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoScreen(
    navController: NavController,
    videoId: String,
    videoViewModel: VideoViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val orientation = LocalScreenOrientation.current
    val context = LocalContext.current as Activity
    var fullscreen by remember {
        mutableStateOf(false)
    }

    // 判断视频是否加载了
    fun isVideoLoaded() =
        videoViewModel.videoDetail != VideoDetail.LOADING && !videoViewModel.error && !videoViewModel.isLoading

    fun getTitle() =
        if (videoViewModel.isLoading) "加载中" else if (isVideoLoaded()) videoViewModel.videoDetail.title else if (videoViewModel.error) "加载失败" else "视频页面"

    val videoLink =
        if (isVideoLoaded() && videoViewModel.videoDetail != VideoDetail.PRIVATE && videoViewModel.videoDetail.videoLinks.isNotEmpty()) videoViewModel.videoDetail.videoLinks[0].toLink() else ""

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // 加载视频
    LaunchedEffect(Unit) {
        videoViewModel.loadVideo(videoId)
    }

    // 响应旋转
    val systemUiController = rememberSystemUiController()
    val primaryColor = MaterialTheme.colors.uiBackGroundColor
    val dark = MaterialTheme.colors.isLight

    LaunchedEffect(orientation) {
        fullscreen = orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    LaunchedEffect(fullscreen) {
        if (fullscreen) {
            context.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            context.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            systemUiController.setNavigationBarColor(primaryColor, darkIcons = dark)
            systemUiController.setStatusBarColor(Color.Transparent, darkIcons = dark)
        }
    }

    // 处理返回
    BackHandler(fullscreen) {
        context.requestedOrientation = Configuration.ORIENTATION_PORTRAIT
        scope.launch {
            delay(1500)
            context.requestedOrientation = -1
        }
        fullscreen = false
    }

    // 取消强制屏幕方向
    DisposableEffect(Unit) {
        onDispose {
            context.requestedOrientation = -1
        }
    }

    Scaffold(
        topBar = {
            if (!fullscreen) {
                FullScreenTopBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    title = {
                        Text(text = getTitle(), maxLines = 1)
                    },
                    actions = {
                        AnimatedVisibility(isVideoLoaded()) {
                            IconButton(onClick = {
                                if (exoPlayer.isReady) {
                                    fullscreen = true
                                    if (!exoPlayer.videoSize.isVertVideo()) {
                                        context.requestedOrientation =
                                            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
                                    }
                                } else {
                                    Toast.makeText(context, "视频还在加载，请稍后...", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }) {
                                Icon(Icons.Default.Fullscreen, null)
                            }
                        }
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsWithImePadding()
        ) {
            ExoPlayer(
                modifier = if (!fullscreen)
                    Modifier
                        .animateContentSize()
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                else
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                videoLink = videoLink,
                exoPlayer = exoPlayer
            )

            when {
                videoViewModel.videoDetail == VideoDetail.PRIVATE -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "这个视频已经被作者上锁，无法观看", fontWeight = FontWeight.Bold)
                    }
                }
                isVideoLoaded() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        VideoInfo(navController, videoViewModel, videoViewModel.videoDetail)
                    }
                }
                videoViewModel.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.nico))
                        LottieAnimation(modifier = Modifier.size(170.dp), composition = composition)
                    }
                }
                videoViewModel.error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable { videoViewModel.loadVideo(videoId) },
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
                            Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun VideoInfo(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val pagerState = rememberPagerState(pageCount = 3, initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem(pagerState, 0, "简介")
            TabItem(pagerState, 1, "评论 ${videoDetail.comments}")
            TabItem(pagerState, 2, "相似推荐")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth(),
                state = pagerState
            ) {
                when (it) {
                    0 -> VideoDescription(navController, videoViewModel, videoDetail)
                    1 -> CommentPage(navController, videoViewModel)
                    2 -> RecommendVideoList(navController, videoDetail)
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun RecommendVideoList(navController: NavController, videoDetail: VideoDetail) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(videoDetail.recommendVideo.filter { it.title.isNotEmpty() }) {
            Card(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
                    .height(120.dp),
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            println(it.id)
                            navController.navigate("video/${it.id}")
                        }
                ) {
                    val painter = rememberImagePainter(it.pic)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .placeholder(visible = painter.state is ImagePainter.State.Loading)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = it.title, maxLines = 1)
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            Text(
                                text = "播放: ${it.watchs} 喜欢: ${it.likes}",
                                maxLines = 1,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun VideoDescription(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 视频简介
        Card(modifier = Modifier.padding(8.dp), elevation = 2.dp) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .padding(8.dp)
            ) {
                // 作者信息
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 作者头像
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .noRippleClickable {
                                navController.navigate("user/${videoDetail.authorId}")
                            }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = rememberImagePainter(videoDetail.authorPic),
                            contentDescription = null
                        )
                    }

                    // 作者名字
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .noRippleClickable {
                                navController.navigate("user/${videoDetail.authorId}")
                            },
                        text = videoDetail.authorName,
                        fontWeight = FontWeight.Bold,
                        color = PINK
                    )

                    // 关注
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .clickable {
                                videoViewModel.handleFollow { action, success ->
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
                                if (videoDetail.follow) Color.LightGray else Color(
                                    0xfff45a8d
                                )
                            )
                            .padding(2.dp),
                    ) {
                        Text(
                            text = if (videoDetail.follow) "已关注" else "+ 关注",
                            color = if (videoDetail.follow) Color.Black else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 视频标题
                Text(text = videoDetail.title, fontSize = 20.sp)

                // 视频信息
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = "播放: ${videoDetail.watchs} 喜欢: ${videoDetail.likes}")
                }

                Spacer(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color.Gray.copy(0.2f))
                )

                // 视频介绍
                var expand by remember {
                    mutableStateOf(false)
                }
                Crossfade(expand) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .noRippleClickable { expand = !expand }
                            .padding(vertical = 4.dp)
                    ) {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                            SelectionContainer {
                                SmartLinkText(
                                    text = videoDetail.description,
                                    maxLines = if (expand) 10 else 1
                                )
                            }
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                modifier = Modifier.size(20.dp),
                                onClick = { expand = !expand }) {
                                Icon(
                                    imageVector = if (it) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

                // 操作按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                videoViewModel.handleLike { action, success ->
                                    if (action) {
                                        Toast
                                            .makeText(
                                                context,
                                                if (success) "点赞大成功！ ヾ(≧▽≦*)o" else "点赞失败",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                if (success) "已取消点赞" else "取消点赞失败",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                            }, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (videoDetail.isLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = if (videoDetail.isLike) Color(0xfff45a8d) else Color.LightGray
                        )
                        Text(text = if (videoDetail.isLike) "已喜欢" else "喜欢", fontSize = 12.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("playlist?nid=${videoDetail.nid}")
                            }, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FeaturedPlayList, null, Modifier.size(15.dp))
                        Text(text = "播单", fontSize = 12.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { context.shareMedia(MediaType.VIDEO, videoDetail.id) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Share, null, Modifier.size(15.dp))
                        Text(text = "分享", fontSize = 12.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                Toast
                                    .makeText(context, "还没写...", Toast.LENGTH_SHORT)
                                    .show()
                            }, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Download, null, Modifier.size(15.dp))
                        Text(text = "下载", fontSize = 12.sp)
                    }
                }
            }
        }

        // 更多视频
        Text(
            text = "该作者的其他视频:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        videoDetail.moreVideo.chunked(2).forEach {
            Row {
                it.forEach {
                    Card(
                        modifier = Modifier
                            .padding(12.dp)
                            .weight(1f)
                            .height(120.dp),
                        elevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    println(it.id)
                                    navController.navigate("video/${it.id}")
                                }
                        ) {
                            val painter = rememberImagePainter(it.pic)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .placeholder(visible = painter.state is ImagePainter.State.Loading)
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    painter = painter,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(text = it.title, maxLines = 1)
                                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                                    Text(
                                        text = "播放: ${it.watchs} 喜欢: ${it.likes}",
                                        maxLines = 1,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun CommentPage(navController: NavController, videoViewModel: VideoViewModel) {
    val context = LocalContext.current
    val pager = videoViewModel.commentPager.collectAsLazyPagingItems()
    val state = rememberSwipeRefreshState(pager.loadState.refresh == LoadState.Loading)
    if (pager.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { pager.retry() }, contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
                LottieAnimation(modifier = Modifier.size(150.dp), composition = composition)
                Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        val dialog = rememberReplyDialogState()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize(),
                state = state,
                onRefresh = { pager.refresh() }) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (pager.itemCount == 0 && pager.loadState.refresh is LoadState.NotLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(text = "暂无评论", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(pager) {
                        CommentItem(navController, it!!, { comment ->
                            dialog.open(
                                replyTo = comment.authorName,
                                nid = videoViewModel.videoDetail.nid,
                                commentId = comment.commentId,
                                commentPostParam = videoViewModel.videoDetail.commentPostParam
                            )
                        })
                    }

                    when (pager.loadState.append) {
                        LoadState.Loading -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Text(text = "加载中", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .noRippleClickable { pager.retry() }
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier.padding(32.dp),
                onClick = {
                    dialog.open(
                        replyTo = "本视频",
                        nid = videoViewModel.videoDetail.nid,
                        commentId = null,
                        commentPostParam = videoViewModel.videoDetail.commentPostParam
                    )
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Default.Comment, null)
            }

            dialog.materialDialog.build(
                buttons = {
                    positiveButton(if(dialog.posting) "正在提交回复..." else "提交") {
                        if (dialog.content.isNotEmpty()) {
                            if(!dialog.posting) {
                                dialog.posting = true
                                videoViewModel.postReply(
                                    content = dialog.content,
                                    nid = dialog.nid,
                                    commentId = if (dialog.commentId == -1) null else dialog.commentId,
                                    commentPostParam = dialog.commentPostParam
                                ) {
                                    dialog.apply {
                                        posting = false
                                        materialDialog.hide()
                                        content = ""
                                    }
                                    Toast.makeText(context, "回复成功！", Toast.LENGTH_SHORT).show()
                                    pager.refresh()
                                }
                            }
                        } else {
                            Toast.makeText(context, "不能回复空内容!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            ) {
                title("回复: ${dialog.replyTo}")
                input(label = "输入回复内容") {
                    dialog.content = it.trim()
                }
            }
        }
    }
}