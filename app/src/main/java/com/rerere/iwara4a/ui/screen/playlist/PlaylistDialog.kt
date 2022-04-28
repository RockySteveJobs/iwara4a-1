package com.rerere.iwara4a.ui.screen.playlist

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.MaterialDialogState
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.component.rememberMaterialDialogState
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource
import soup.compose.material.motion.MaterialFadeThrough

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
        PlaylistScreen(playlistViewModel, playlistId)
    }
}

@Composable
fun PlaylistDialog(
    playlistViewModel: PlaylistViewModel,
    onSuccess: () -> Unit = {}
): MaterialDialogState {
    val context = LocalContext.current
    var dialog = rememberMaterialDialogState()
    var title by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    if(dialog.isVisible()){
        AlertDialog(
            onDismissRequest = {
                dialog.hide()
            },
            title = {
                Text(stringResource(id = R.string.screen_playlist_create_title))
            },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text(text = stringResource(id = R.string.screen_playlist_create_label))
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!playlistViewModel.creatingPlaylist) {
                            playlistViewModel.createPlaylist(scope, title) {
                                Toast.makeText(
                                    context,
                                    "${context.stringResource(id = R.string.screen_playlist_create_create)}${
                                        if (it) context.stringResource(id = R.string.success) else context.stringResource(
                                            id = R.string.fail
                                        )
                                    }",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.hide()
                                title = ""
                                onSuccess()
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (playlistViewModel.creatingPlaylist) "${stringResource(id = R.string.screen_playlist_create_creating)}..." else stringResource(
                            id = R.string.confirm_button
                        )
                    )
                }
            }
        )
    }
    return dialog
}

@Composable
fun PlaylistDetail(
    modifier: Modifier = Modifier,
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
    Column(modifier.padding(16.dp)) {
        Text(
            text = "${stringResource(id = R.string.screen_playlist_detail_column)}: ${if (videoList !is DataState.Success) "???" else videoList.read().title}",
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = WindowInsets.navigationBars.asPaddingValues()
                        ) {
                            items(it.read().videolist) {
                                MediaPreviewCard(navController = navController, mediaPreview = it)
                            }
                        }
                    }
                    is DataState.Error -> {
                        TextButton(onClick = {
                            playlistViewModel.loadDetail(playlistId)
                        }) {
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
}

@Composable
fun PlaylistExplore(
    navController: NavController,
    playlistViewModel: PlaylistViewModel
) {
    Column(Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.screen_playlist_explore_column),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        val playlistOverviewList by playlistViewModel.overview.collectAsState()
        LaunchedEffect(Unit) {
            playlistViewModel.loadOverview()
        }
        MaterialFadeThrough(targetState = playlistOverviewList) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (it) {
                    is DataState.Empty, is DataState.Loading -> {
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
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(
                                isRefreshing = playlistOverviewList is DataState.Loading
                            ),
                            onRefresh = {
                                playlistViewModel.loadOverview()
                            }
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = WindowInsets.navigationBars.asPaddingValues()
                            ) {
                                items(it.read()) {
                                    ElevatedCard(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .combinedClickable(
                                                onClick = {
                                                    navController.navigate("playlist?playlist-id=${it.id}")
                                                }
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Outlined.Menu, null)
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Text(
                                                text = it.name,
                                                modifier = Modifier.weight(1f),
                                                fontSize = 20.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is DataState.Error -> {
                        Text(
                            text = "${stringResource(id = R.string.screen_playlist_explore_load_fail)}: ${(playlistOverviewList as DataState.Error).message}"
                        )
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

    val dialog = PlaylistDialog(
        playlistViewModel = playlistViewModel,
    ) {
        playlistViewModel.loadPlaylist(nid)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_playlist_edit_add),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    MaterialFadeThrough(targetState = playlistViewModel.modifyLoading) {
                        if (it) {
                            CircularProgressIndicator(modifier = Modifier.size(25.dp))
                        } else {
                            IconButton(
                                modifier = Modifier.size(25.dp),
                                onClick = {
                                    dialog.show()
                                })
                            {
                                Icon(Icons.Outlined.Add, null)
                            }
                        }
                    }
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Outlined.Close, null)
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
                    Text(text = stringResource(id = R.string.screen_playlist_edit_load_fail))
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
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = playlist.title, modifier = Modifier.weight(1f))
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