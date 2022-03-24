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
import com.rerere.iwara4a.ui.local.LocalNavController

@Composable
fun VideoScreenSimilarVideoTab(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(videoDetail.recommendVideo.filter { it.title.isNotEmpty() }) {
            ElevatedCard(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth(),
                onClick = {
                    navController.navigate("video/${it.id}")
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16/9f),
                        model = it.pic,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = it.title, maxLines = 1)
                        Text(
                            text = "${stringResource(id = R.string.screen_video_views)}: ${it.watchs} ${
                                stringResource(
                                    id = R.string.screen_video_likes
                                )
                            }: ${it.likes}",
                            maxLines = 1,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}