package com.rerere.iwara4a.ui.screen.video.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.local.LocalNavController

@Composable
fun VideoScreenSimilarVideoTab(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.navigationBars.asPaddingValues()
    ) {
        items(videoDetail.recommendVideo.filter { it.title.isNotEmpty() }) {
            MediaPreviewCard(
                navController, MediaPreview(
                    title = it.title,
                    author = "",
                    previewPic = it.pic,
                    likes = it.likes,
                    watchs = it.watchs,
                    type = MediaType.VIDEO,
                    mediaId = it.id
                )
            )
        }
    }
}