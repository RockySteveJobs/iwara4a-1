package com.rerere.iwara4a.ui.screen.video.tabs

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rerere.iwara4a.data.model.detail.video.VideoDetail
import com.rerere.iwara4a.data.model.index.MediaPreview
import com.rerere.iwara4a.data.model.index.MediaType
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.local.LocalNavController

@Composable
fun VideoScreenSimilarVideoTab(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive(140.dp),
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