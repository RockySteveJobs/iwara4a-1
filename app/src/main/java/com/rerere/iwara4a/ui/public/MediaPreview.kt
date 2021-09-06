package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType

@Composable
fun MediaPreviewCard(navController: NavController, mediaPreview: MediaPreview) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (mediaPreview.type == MediaType.VIDEO) {
                    navController.navigate("video/${mediaPreview.mediaId}")
                } else if (mediaPreview.type == MediaType.IMAGE) {
                    navController.navigate("image/${mediaPreview.mediaId}")
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp), contentAlignment = Alignment.BottomCenter
            ) {
                val coilPainter = rememberImagePainter(mediaPreview.previewPic)
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .placeholder(
                            visible = coilPainter.state is ImagePainter.State.Loading,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                        .let {
                            if (mediaPreview.private) {
                                // 如果这个视频是私有视频，将其封面图片模糊化
                                it.blur(5.dp)
                            } else it
                        },
                    painter = coilPainter,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
                if(mediaPreview.private){
                    Text(
                        text = "私有视频",
                        modifier = Modifier.align(Alignment.Center),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    val (plays, likes, type) = createRefs()

                    Row(modifier = Modifier.constrainAs(plays) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.play_icon),
                            contentDescription = null
                        )
                        Text(text = mediaPreview.watchs, fontSize = 13.sp)
                    }

                    Row(modifier = Modifier.constrainAs(likes) {
                        start.linkTo(plays.end, 8.dp)
                        bottom.linkTo(parent.bottom)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(R.drawable.like_icon),
                            contentDescription = null
                        )
                        Text(text = mediaPreview.likes, fontSize = 13.sp)
                    }

                    Row(modifier = Modifier.constrainAs(type) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (mediaPreview.type) {
                                MediaType.VIDEO -> "视频"
                                MediaType.IMAGE -> "图片"
                            }, fontSize = 13.sp
                        )
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp)
            ) {
                Text(text = mediaPreview.title.trimStart(), maxLines = 1)
                Spacer(modifier = Modifier.height(3.dp))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
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
}