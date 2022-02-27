package com.rerere.iwara4a.ui.screen.video.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.material.placeholder
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.ui.local.LocalNavController

@Composable
fun VideoScreenSimilarVideoTab(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(videoDetail.recommendVideo.filter { it.title.isNotEmpty() }) {
            ElevatedCard(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("video/${it.id}")
                        }
                ) {
                    val painter = rememberImagePainter(it.pic)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .placeholder(visible = painter.state is ImagePainter.State.Loading)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = it.title, maxLines = 1, fontWeight = FontWeight.Bold)
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