package com.rerere.iwara4a.ui.screen.playlist

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.playlist.PlaylistDetail
import com.rerere.iwara4a.model.playlist.PlaylistOverview
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.util.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import soup.compose.material.motion.MaterialFadeThrough
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun PlaylistDialog(
    navController: NavController,
    nid: Int,
    playlistId: String,
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {
    if (nid > 0) {
        // 根据视频编辑播单
        EditPlaylist(
            navController = navController,
            playlistViewModel = playlistViewModel,
            nid = nid
        )
    } else {
        Scaffold(
            topBar = {
                DefTopBar(navController, "播单")
            }
        ) {
            // 浏览播单
            Box(modifier = Modifier.navigationBarsPadding()) {
                if (playlistId.isNotEmpty()) {
                    PlaylistDetail(
                        playlistId = playlistId,
                        navController = navController,
                        playlistViewModel = playlistViewModel
                    )
                } else {
                    PlaylistExplore(
                        navController = navController,
                        playlistViewModel = playlistViewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun PlaylistDetail(
    playlistId: String,
    navController: NavController,
    playlistViewModel: PlaylistViewModel
) {
    val videoList by playlistViewModel.playlistDetail.collectAsState()
    LaunchedEffect(playlistId) {
        if (videoList !is DataState.Success) {
            playlistViewModel.loadDetail(playlistId)
        }
    }
    Column(Modifier.padding(16.dp)) {
        Text(
            text = "播单: ${if (videoList !is DataState.Success) "???" else videoList.read().title}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        MaterialFadeThrough(targetState = videoList) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (it) {
                    is DataState.Empty,
                    is DataState.Loading -> {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.chip
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.Center),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    }
                    is DataState.Success -> {
                        LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                            items(it.read().videolist) {
                                MediaPreviewCard(navController = navController, mediaPreview = it)
                            }
                        }
                    }
                    is DataState.Error -> {
                        TextButton(onClick = {
                            playlistViewModel.loadDetail(playlistId)
                        }) {
                            Text(text = "加载失败！点击重新加载！", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlaylistExplore(
    navController: NavController,
    playlistViewModel: PlaylistViewModel
) {
    Column(Modifier.padding(16.dp)) {
        Text(text = "播单列表", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        val playlistOverviewList by playlistViewModel.overview.collectAsState()
        LaunchedEffect(Unit){
            if(playlistOverviewList !is DataState.Success){
                playlistViewModel.loadOverview()
            }
        }
        MaterialFadeThrough(targetState = playlistOverviewList) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (it) {
                    is DataState.Empty,
                    is DataState.Loading -> {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.chip
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier
                                .size(300.dp)
                                .align(Alignment.Center),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    }
                    is DataState.Success -> {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(it.read()) {
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            navController.navigate("playlist?playlist-id=${it.id}")
                                        },
                                    elevation = 3.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = it.name)
                                    }
                                }
                            }
                        }
                    }
                    is DataState.Error -> {
                        Text(text = "加载播单失败: ${(playlistOverviewList as DataState.Error).message}")
                    }
                }
            }
        }
    }
}

// 编辑某个视频到播单
@Composable
private fun EditPlaylist(
    navController: NavController,
    playlistViewModel: PlaylistViewModel,
    nid: Int
) {
    LaunchedEffect(Unit) {
        playlistViewModel.loadPlaylist(nid)
    }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(400.dp)
            .height(500.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = 4.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "添加到播单",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                // modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(25.dp))
                            Crossfade(playlistViewModel.modifyLoading) {
                                if (it) {
                                    CircularProgressIndicator(modifier = Modifier.size(25.dp))
                                }
                            }
                        }
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                }
                if (playlistViewModel.modifyPlaylistLoading) {
                    items(2) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(4.dp)
                                .placeholder(
                                    visible = true,
                                    highlight = PlaceholderHighlight.shimmer()
                                )
                        )
                    }
                }

                if (playlistViewModel.modifyPlaylistError) {
                    item {
                        Text(text = "加载失败！请稍后重试！")
                    }
                }
                items(playlistViewModel.modifyPlaylist) { playlist ->
                    Row(
                        modifier = Modifier
                            .clickable {
                                playlistViewModel.modify(
                                    context,
                                    playlist.nid.toInt(),
                                    nid,
                                    playlist.inIt
                                )
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = playlist.title, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(20.dp))
                        Checkbox(checked = playlist.inIt, onCheckedChange = {
                            playlistViewModel.modify(
                                context,
                                playlist.nid.toInt(),
                                nid,
                                playlist.inIt
                            )
                        })
                    }
                }
            }
        }
    }
}