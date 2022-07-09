package com.rerere.iwara4a.ui.screen.playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.*
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    playlistViewModel: PlaylistViewModel,
    playlistId: String
) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val dialog = PlaylistDialog(playlistViewModel = playlistViewModel) {
        // refresh
        if (playlistId.isNotEmpty()) {
            playlistViewModel.loadDetail(playlistId)
        } else {
            playlistViewModel.loadOverview()
        }
    }
    Scaffold(
        topBar = {
            TopBar(playlistViewModel, playlistId, dialog)
        }
    ) { padding ->
        if (playlistId.isNotEmpty()) {
            // 浏览播单内容
            PlaylistDetail(
                modifier = Modifier.padding(padding),
                playlistId = playlistId,
                navController = navController,
                playlistViewModel = playlistViewModel
            )
        } else {
            // 浏览播单列表
            val pager = rememberPagerState()
            Column(
                modifier = Modifier.padding(padding)
            ) {
                TabRow(
                    selectedTabIndex = pager.currentPage,
                    indicator = {
                        TabRowDefaults.Indicator(
                            Modifier.pagerTabIndicatorOffset(pager, it)
                        )
                    }
                ) {
                    Tab(
                        selected = pager.currentPage == 0,
                        onClick = { scope.launch { pager.scrollToPage(0) } },
                        text = {
                            Text("我的播单")
                        }
                    )
                    Tab(
                        selected = pager.currentPage == 1,
                        onClick = { scope.launch { pager.scrollToPage(1) } },
                        text = {
                            Text("公共播单")
                        }
                    )
                }
                HorizontalPager(
                    count = 2,
                    state = pager
                ) {
                    when (it) {
                        0 -> {
                            PlaylistExplore(
                                navController = navController,
                                playlistViewModel = playlistViewModel
                            )
                        }
                        1 -> {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    playlistViewModel: PlaylistViewModel,
    playlistId: String,
    dialog: MaterialDialogState
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    Md3TopBar(
        navigationIcon = {
            BackIcon()
        },
        title = {
            Text(text = stringResource(id = R.string.screen_playlist_dialog_topbar_title))
        },
        actions = {
            val detail by playlistViewModel.playlistDetail.collectAsState()
            val deleteDialog = rememberMaterialDialogState()
            if (deleteDialog.isVisible()) {
                AlertDialog(
                    onDismissRequest = {
                        deleteDialog.hide()
                    },
                    title = {
                        Text(stringResource(id = R.string.screen_playlist_dialog_delete_title))
                    },
                    text = {
                        Text("${stringResource(id = R.string.screen_playlist_dialog_delete_message)} ${detail.readSafely()?.title ?: "<未知>"}")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            deleteDialog.hide()
                            playlistViewModel.deletePlaylist(scope) {
                                if (it) {
                                    navController.popBackStack()
                                    Toast.makeText(
                                        context,
                                        context.stringResource(id = R.string.screen_playlist_dialog_delete_success),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.stringResource(id = R.string.screen_playlist_dialog_delete_failed),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }) {
                            Text(stringResource(id = R.string.confirm_button))
                        }
                    }
                )
            }
            val editDialog = rememberMaterialDialogState()
            var title by remember {
                mutableStateOf("")
            }
            if(editDialog.isVisible()) {
                AlertDialog(
                    onDismissRequest = { editDialog.hide() },
                    title = {
                        Text(stringResource(id = R.string.screen_playlist_dialog_rename_title))
                    },
                    text = {
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (title.isNotBlank()) {
                                    editDialog.hide()
                                    playlistViewModel.changePlaylistName(scope, title) {
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
                                    Toast.makeText(
                                        context,
                                        context.stringResource(id = R.string.screen_playlist_dialog_rename_empty),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        ) {
                            Text(stringResource(R.string.save_button))
                        }
                    }
                )
            }
            if (detail is DataState.Success) {
                IconButton(
                    onClick = {
                        editDialog.show()
                    }
                ) {
                    Icon(Icons.Outlined.Edit, null)
                }
                IconButton(
                    onClick = {
                        deleteDialog.show()
                    }
                ) {
                    Icon(Icons.Outlined.Delete, null)
                }
            } else {
                IconButton(
                    onClick = { dialog.show() }
                ) {
                    Icon(Icons.Outlined.Add, null)
                }
            }
        }
    )
}