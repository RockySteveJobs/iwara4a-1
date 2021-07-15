package com.rerere.iwara4a.ui.screen.playlist

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rerere.iwara4a.ui.theme.uiBackGroundColor

@Composable
fun PlaylistDialog(navController: NavController, playlistViewModel: PlaylistViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "播单")
                },
                backgroundColor = MaterialTheme.colors.uiBackGroundColor
            )
        }
    ) {
        Text(text = "还没写 😅")
    }
}