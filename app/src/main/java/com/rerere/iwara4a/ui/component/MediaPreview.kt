package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType

@Composable
fun MediaPreviewCard(navController: NavController, mediaPreview: MediaPreview) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            if (mediaPreview.type == MediaType.VIDEO) {
                navController.navigate("video/${mediaPreview.mediaId}")
            } else if (mediaPreview.type == MediaType.IMAGE) {
                navController.navigate("image/${mediaPreview.mediaId}")
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .let {
                            if (mediaPreview.private) {
                                // 如果这个视频是私有视频，将其封面图片模糊化
                                it.blur(5.dp)
                            } else it
                        },
                    model = mediaPreview.previewPic,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
                if (mediaPreview.private) {
                    Text(
                        text = "私有视频",
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(R.drawable.play_icon),
                        contentDescription = null
                    )
                    Text(text = mediaPreview.watchs, fontSize = 13.sp)
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(R.drawable.like_icon),
                        contentDescription = null
                    )
                    Text(text = mediaPreview.likes, fontSize = 13.sp)
                    Text(
                        modifier = Modifier.weight(1f),
                        text = when (mediaPreview.type) {
                            MediaType.VIDEO -> stringResource(R.string.video)
                            MediaType.IMAGE -> stringResource(R.string.image)
                        },
                        fontSize = 13.sp,
                        textAlign = TextAlign.End
                    )
                }

                Text(text = mediaPreview.title.trim(), maxLines = 1, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(17.dp),
                        painter = painterResource(R.drawable.upzhu),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(1.dp))
                    Text(text = mediaPreview.author, maxLines = 1, fontSize = 13.sp)
                }
            }
        }
    }
}