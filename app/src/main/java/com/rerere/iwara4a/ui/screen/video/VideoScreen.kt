package com.rerere.iwara4a.ui.screen.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.ui.component.DKComposePlayer
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.screen.video.tabs.VideoScreenCommentTab
import com.rerere.iwara4a.ui.screen.video.tabs.VideoScreenDetailTab
import com.rerere.iwara4a.ui.screen.video.tabs.VideoScreenSimilarVideoTab
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.noRippleClickable
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.launch
import soup.compose.material.motion.MaterialFadeThrough

@Composable
fun VideoScreen(
    navController: NavController,
    videoId: String,
    videoViewModel: VideoViewModel = hiltViewModel()
) {
    val videoDetail by videoViewModel.videoDetailState.collectAsState()
    val context = LocalContext.current // using getTitle

    // 判断视频是否加载了
    fun isVideoLoaded() = videoDetail is DataState.Success

    fun getTitle() = when {
        isVideoLoaded() -> videoDetail.read().title
        videoDetail is DataState.Loading -> context.stringResource(id = R.string.loading)
        videoDetail is DataState.Error -> context.stringResource(id = R.string.load_error)
        else -> context.stringResource(id = R.string.screen_video_title_video_page)
    }

    // 加载视频
    LaunchedEffect(Unit) {
        if (!isVideoLoaded()) {
            videoViewModel.loadVideo(videoId)
        }
    }

    Scaffold(
        topBar = {
            Md3TopBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                title = {
                    Text(text = getTitle(), maxLines = 1)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsWithImePadding()
        ) {
            DKComposePlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f),
                title = getTitle(),
                link = if (isVideoLoaded()) videoDetail.read().videoLinks.toDKLink() else emptyMap()
            )

            MaterialFadeThrough(
                targetState = videoDetail
            ) {
                when (it) {
                    is DataState.Empty,
                    is DataState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
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
                    is DataState.Success -> {
                        if (it.read() == VideoDetail.PRIVATE) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.screen_video_detail_private),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                VideoInfo(navController, videoViewModel, it.read())
                            }
                        }
                    }
                    is DataState.Error -> {
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
                                Text(
                                    text = "${stringResource(id = R.string.load_error)}~ （${
                                        stringResource(
                                            id = R.string.screen_video_detail_error_daily_potato
                                        )
                                    }）", fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoInfo(
    navController: NavController,
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val pagerState = rememberPagerState(0)
    val coroutineScope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(text = stringResource(R.string.introduction))
                }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    BadgedBox(
                        badge = {
                            Badge {
                                Text(
                                    text = videoDetail.comments.toString()
                                )
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.comment))
                    }
                }
            )
            Tab(
                selected = pagerState.currentPage == 2,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                },
                text = {
                    Text(text = stringResource(R.string.similar_video))
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth(),
                state = pagerState,
                count = 3
            ) {
                when (it) {
                    0 -> VideoScreenDetailTab(videoViewModel, videoDetail)
                    1 -> VideoScreenCommentTab(navController, videoViewModel)
                    2 -> VideoScreenSimilarVideoTab(videoDetail)
                }
            }
        }
    }
}


