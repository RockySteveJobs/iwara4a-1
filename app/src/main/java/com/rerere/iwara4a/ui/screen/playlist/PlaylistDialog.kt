package com.rerere.iwara4a.ui.screen.playlist

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import soup.compose.material.motion.MaterialFadeThrough
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalFoundationApi::class)
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
        val dialog = createPlaylistDialog(playlistViewModel = playlistViewModel){
            // refresh
            if(playlistId.isNotEmpty()){
                playlistViewModel.loadDetail(playlistId)
            } else {
                playlistViewModel.loadOverview()
            }
        }
        Scaffold(
            topBar = {
                FullScreenTopBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    title = {
                        Text(text = "播单")
                    },
                    actions = {
                        IconButton(onClick = { dialog.show()}) {
                            Icon(Icons.Default.Add, null)
                        }
                    }
                )
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

@Composable
private fun createPlaylistDialog(playlistViewModel: PlaylistViewModel, onSuccess: () -> Unit = {}): MaterialDialog {
    val context = LocalContext.current
    val dialog = remember {
        MaterialDialog()
    }
    var title by remember {
        mutableStateOf("")
    }
    dialog.build(
        buttons = {
            positiveButton(if (playlistViewModel.creatingPlaylist) "创建播单中..." else "确定") {
                if (!playlistViewModel.creatingPlaylist) {
                    playlistViewModel.createPlaylist(title) {
                        Toast.makeText(context, "创建播单${if (it) "成功" else "失败"}", Toast.LENGTH_SHORT)
                            .show()
                        dialog.hide()
                        title = ""
                        onSuccess()
                    }
                }
            }
            negativeButton("取消") {
                dialog.hide()
            }
        }
    ) {
        title("创建播单")
        customView {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text(text = "输入播单名字")
                }
            )
        }
    }
    return dialog
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

@ExperimentalFoundationApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlaylistExplore(
    navController: NavController,
    playlistViewModel: PlaylistViewModel
) {
    val context = LocalContext.current
    Column(Modifier.padding(16.dp)) {
        Text(text = "播单列表", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        val playlistOverviewList by playlistViewModel.overview.collectAsState()
        LaunchedEffect(Unit) {
            if (playlistOverviewList !is DataState.Success) {
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
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(it.read()) {
                                var showMenu by remember {
                                    mutableStateOf(false)
                                }
                                Surface(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("playlist?playlist-id=${it.id}")
                                            },
                                            onLongClick = {
                                                showMenu = !showMenu
                                            }
                                        ),
                                    elevation = 3.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.FeaturedPlayList, null)
                                        Spacer(modifier = Modifier.width(15.dp))
                                        Text(
                                            text = it.name,
                                            modifier = Modifier.weight(1f),
                                            fontSize = 20.sp
                                        )
                                        AnimatedVisibility(visible = showMenu) {
                                            IconButton(onClick = {
                                                Toast.makeText(context, "还没做这个功能", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                    Modifier.alpha(ContentAlpha.medium)
                                                )
                                            }
                                        }
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
        val dialog = createPlaylistDialog(playlistViewModel = playlistViewModel){
            playlistViewModel.loadPlaylist(nid)
        }
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
                                modifier = Modifier.weight(1f)
                            )
                            Crossfade(targetState = playlistViewModel.modifyLoading) {
                                if (it) {
                                    CircularProgressIndicator(modifier = Modifier.size(25.dp))
                                } else {
                                    IconButton(modifier = Modifier.size(25.dp) ,onClick = {
                                        dialog.show()
                                    }) {
                                        Icon(Icons.Default.Add, null)
                                    }
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