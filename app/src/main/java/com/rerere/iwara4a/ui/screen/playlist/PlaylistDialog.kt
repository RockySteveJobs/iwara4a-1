package com.rerere.iwara4a.ui.screen.playlist

import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.IwaraTopBar
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource
import com.vanpra.composematerialdialogs.*
import soup.compose.material.motion.MaterialFadeThrough

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistDialog(
    navController: NavController,
    nid: Int,
    playlistId: String,
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    if (nid > 0) {
        // 根据视频编辑播单
        EditPlaylist(
            navController = navController,
            playlistViewModel = playlistViewModel,
            nid = nid
        )
    } else {
        val dialog = createPlaylistDialog(playlistViewModel = playlistViewModel) {
            // refresh
            if (playlistId.isNotEmpty()) {
                playlistViewModel.loadDetail(playlistId)
            } else {
                playlistViewModel.loadOverview()
            }
        }
        Scaffold(
            topBar = {
                IwaraTopBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    },
                    title = {
                        Text(text = stringResource(id = R.string.screen_playlist_dialog_topbar_title))
                    },
                    actions = {
                        val detail by playlistViewModel.playlistDetail.collectAsState()
                        val deleteDialog = rememberMaterialDialogState()
                        MaterialDialog(
                            dialogState = deleteDialog,
                            buttons = {
                                positiveButton(stringResource(id = R.string.confirm_button)) {
                                    deleteDialog.hide()
                                    playlistViewModel.deletePlaylist {
                                        if (it) {
                                            navController.popBackStack()
                                            Toast.makeText(context, context.stringResource(id = R.string.screen_playlist_dialog_delete_success), Toast.LENGTH_SHORT)
                                                .show()
                                        } else {
                                            Toast.makeText(context, context.stringResource(id = R.string.screen_playlist_dialog_delete_failed), Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                }
                                negativeButton(stringResource(id = R.string.cancel_button)) {
                                    deleteDialog.hide()
                                }
                            }
                        ) {
                            title(stringResource(id = R.string.screen_playlist_dialog_delete_title))
                            message("${stringResource(id = R.string.screen_playlist_dialog_delete_message)} ${detail.readSafely()?.title ?: "<未知>"}")
                        }
                        val editDialog = rememberMaterialDialogState()
                        var title by remember {
                            mutableStateOf("")
                        }
                        MaterialDialog(
                            dialogState = editDialog,
                            buttons = {
                                positiveButton(stringResource(id = R.string.save_button)) {
                                    if (title.isNotBlank()) {
                                        editDialog.hide()
                                        playlistViewModel.changePlaylistName(title) {
                                            if (it) {
                                                Toast.makeText(
                                                    context,
                                                    context.stringResource(id = R.string.screen_playlist_dialog_rename_success),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                playlistViewModel.loadDetail(playlistId)
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    context.stringResource(id = R.string.screen_playlist_dialog_rename_failed),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, context.stringResource(id = R.string.screen_playlist_dialog_rename_empty), Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                                negativeButton(stringResource(id = R.string.cancel_button)) {
                                    editDialog.hide()
                                }
                            }
                        ) {
                            title(stringResource(id = R.string.screen_playlist_dialog_rename_title))
                            customView {
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = {
                                        title = it
                                    }
                                )
                            }
                        }
                        if (detail is DataState.Success) {
                            IconButton(onClick = {
                                editDialog.show()
                            }) {
                                Icon(Icons.Default.Edit, null)
                            }
                            IconButton(onClick = {
                                deleteDialog.show()
                            }) {
                                Icon(Icons.Default.Delete, null)
                            }
                        } else {
                            IconButton(onClick = { dialog.show() }) {
                                Icon(Icons.Default.Add, null)
                            }
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
private fun createPlaylistDialog(
    playlistViewModel: PlaylistViewModel,
    onSuccess: () -> Unit = {}
): MaterialDialogState {
    val context = LocalContext.current
    val dialog = rememberMaterialDialogState()
    var title by remember {
        mutableStateOf("")
    }
    MaterialDialog(
        dialogState = dialog,
        buttons = {
            positiveButton(if (playlistViewModel.creatingPlaylist) "${stringResource(id = R.string.screen_playlist_create_creating)}..." else stringResource(id = R.string.confirm_button)) {
                if (!playlistViewModel.creatingPlaylist) {
                    playlistViewModel.createPlaylist(title) {
                        Toast.makeText(context, "${context.stringResource(id = R.string.screen_playlist_create_create)}${if (it) context.stringResource(id = R.string.success) else context.stringResource(id = R.string.fail)}", Toast.LENGTH_SHORT)
                            .show()
                        dialog.hide()
                        title = ""
                        onSuccess()
                    }
                }
            }
            negativeButton(stringResource(id = R.string.cancel_button)) {
                dialog.hide()
            }
        }
    ) {
        title(stringResource(id = R.string.screen_playlist_create_title))
        customView {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text(text = stringResource(id = R.string.screen_playlist_create_label))
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
                            Text(text = stringResource(id = R.string.load_error), fontWeight = FontWeight.Bold)
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
        Text(text = stringResource(id = R.string.screen_playlist_explore_column), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        val playlistOverviewList by playlistViewModel.overview.collectAsState()
        LaunchedEffect(Unit) {
            playlistViewModel.loadOverview()
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
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing = playlistOverviewList is DataState.Loading),
                            onRefresh = {
                                playlistViewModel.loadOverview()
                            }
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(it.read()) {
                                    Surface(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .combinedClickable(
                                                onClick = {
                                                    navController.navigate("playlist?playlist-id=${it.id}")
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
                                            Icon(Icons.Default.Menu, null)
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
                        Text(text = "${stringResource(id = R.string.screen_playlist_explore_load_fail)}: ${(playlistOverviewList as DataState.Error).message}")
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
        val dialog = createPlaylistDialog(
            playlistViewModel = playlistViewModel,
        ) {
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
                                text = stringResource(id = R.string.screen_playlist_edit_add),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Crossfade(targetState = playlistViewModel.modifyLoading) {
                                if (it) {
                                    CircularProgressIndicator(modifier = Modifier.size(25.dp))
                                } else {
                                    IconButton(modifier = Modifier.size(25.dp), onClick = {
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