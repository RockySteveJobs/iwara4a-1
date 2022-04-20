package com.rerere.iwara4a.ui.screen.playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Column
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
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.pagerTabIndicatorOffset
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource
import com.vanpra.composematerialdialogs.*
import kotlinx.coroutines.launch

@Composable
fun PlaylistScreen(
    playlistViewModel: PlaylistViewModel,
    playlistId: String
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
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
    ) {
        if (playlistId.isNotEmpty()) {
            // 浏览播单内容
            PlaylistDetail(
                playlistId = playlistId,
                navController = navController,
                playlistViewModel = playlistViewModel
            )
        } else {
            // 浏览播单列表
            val pager = rememberPagerState()
            Column {
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
            MaterialDialog(
                dialogState = deleteDialog,
                buttons = {
                    positiveButton(stringResource(id = R.string.confirm_button)) {
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