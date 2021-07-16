package com.rerere.iwara4a.ui.screen.playlist

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
fun PlaylistDialog(
    navController: NavController,
    nid: Int,
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {
    if (nid > 0) {
        EditPlaylist(
            navController = navController,
            playlistViewModel = playlistViewModel,
            nid = nid
        )
    } else {
        // TODO: SHOW ALL PLAYLIST
    }
}

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