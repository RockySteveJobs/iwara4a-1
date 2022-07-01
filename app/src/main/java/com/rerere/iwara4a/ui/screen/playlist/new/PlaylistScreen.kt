package com.rerere.iwara4a.ui.screen.playlist.new

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.Md3TopBar

@Composable
fun PlaylistScreen(
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(stringResource(R.string.screen_playlist_dialog_topbar_title))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {

        }
    }
}