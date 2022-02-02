package com.rerere.iwara4a.ui.screen.video.tabs

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.material.placeholder
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.SmartLinkText
import com.rerere.iwara4a.ui.screen.video.VideoViewModel
import com.rerere.iwara4a.ui.theme.PINK
import com.rerere.iwara4a.util.*
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun VideoScreenDetailTab(
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item {
            // 视频简介
            VideoDetail(videoDetail, videoViewModel)
        }

        item {
            // 作者更多视频
            AuthorMoreVideo(videoDetail)
        }
    }
}

@Composable
private fun VideoDetail(videoDetail: VideoDetail, videoViewModel: VideoViewModel) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    Surface {
        Column(
            modifier = Modifier
                .animateContentSize()
        ) {
            // 作者信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 作者头像
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .noRippleClickable {
                            navController.navigate("user/${videoDetail.authorId}")
                        }
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberImagePainter(videoDetail.authorPic),
                        contentDescription = null
                    )
                }

                // 作者名字
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .noRippleClickable {
                                navController.navigate("user/${videoDetail.authorId}")
                            },
                        text = videoDetail.authorName,
                        fontWeight = FontWeight.Bold,
                        color = PINK
                    )
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(text = "在 ${videoDetail.postDate} 上传", fontSize = 12.sp)
                    }
                }

                // 关注
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .clickable {
                            videoViewModel.handleFollow { action, success ->
                                if (action) {
                                    Toast
                                        .makeText(
                                            context,
                                            if (success) "${context.stringResource(id = R.string.follow_success)} ヾ(≧▽≦*)o" else context.stringResource(
                                                id = R.string.follow_fail
                                            ),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            if (success) context.stringResource(id = R.string.unfollow_success) else context.stringResource(
                                                id = R.string.unfollow_fail
                                            ),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                        }
                        .background(
                            if (videoDetail.follow) Color.LightGray else MaterialTheme.colorScheme.primary
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = if (videoDetail.follow) stringResource(id = R.string.follow_status_following) else "+ ${
                            stringResource(
                                id = R.string.follow_status_not_following
                            )
                        }",
                        color = if (videoDetail.follow) Color.Black else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            var expand by remember {
                mutableStateOf(false)
            }

            // 视频标题
            Row(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = videoDetail.title,
                    fontSize = 19.sp,
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable {
                            expand = !expand
                        },
                    maxLines = if (expand) Int.MAX_VALUE else 1
                )
                IconButton(
                    modifier = Modifier.size(20.dp),
                    onClick = { expand = !expand }) {
                    Icon(
                        imageVector = if (expand) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            // 视频信息
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(17.dp),
                        painter = painterResource(R.drawable.play_icon),
                        contentDescription = null
                    )
                    Text(text = videoDetail.watchs, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(
                        modifier = Modifier.size(17.dp),
                        painter = painterResource(R.drawable.like_icon),
                        contentDescription = null
                    )
                    Text(text = videoDetail.likes, fontSize = 15.sp)
                }
            }

            // 视频介绍
            AnimatedVisibility(
                visible = expand,
                enter = fadeIn() + expandVertically(
                    animationSpec = tween(150)
                ),
                exit = fadeOut() + shrinkVertically(
                    animationSpec = tween(150)
                )
            ) {
                SelectionContainer(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp
                        )
                    ) {
                        SmartLinkText(
                            text = videoDetail.description
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 操作按钮
            BottomNavigation(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.background,
                elevation = 0.dp
            ) {
                BottomNavigationItem(
                    selected = videoDetail.isLike,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = LocalContentColor.current.copy(ContentAlpha.medium),
                    onClick = {
                        videoViewModel.handleLike { action, success ->
                            if (action) {
                                Toast
                                    .makeText(
                                        context,
                                        if (success) "${context.stringResource(id = R.string.screen_video_description_liking_success)} ヾ(≧▽≦*)o" else context.stringResource(
                                            id = R.string.screen_video_description_liking_fail
                                        ),
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        if (success) context.stringResource(id = R.string.screen_video_description_unlike_success) else context.stringResource(
                                            id = R.string.screen_video_description_unlike_fail
                                        ),
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(
                            text = if (videoDetail.isLike) stringResource(id = R.string.screen_video_description_like_status_liked) else stringResource(
                                id = R.string.screen_video_description_like_status_no_like
                            )
                        )
                    }
                )
                BottomNavigationItem(
                    selected = false,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = LocalContentColor.current.copy(ContentAlpha.medium),
                    onClick = {
                        navController.navigate("playlist?nid=${videoDetail.nid}")
                    },
                    icon = {
                        Icon(Icons.Default.FeaturedPlayList, null)
                    },
                    label = {
                        Text(text = stringResource(id = R.string.screen_video_description_playlist))
                    }
                )
                BottomNavigationItem(
                    selected = true,
                    onClick = {
                        context.shareMedia(MediaType.VIDEO, videoDetail.id)
                    },
                    icon = {
                        Icon(Icons.Default.Share, null)
                    },
                    label = {
                        Text(text = stringResource(id = R.string.screen_video_description_share))
                    }
                )
                val downloadDialog = rememberMaterialDialogState()
                val exist by produceState(initialValue = false) {
                    value = AppContext.database.getDownloadedVideoDao()
                        .getVideo(videoDetail.nid) != null
                }
                MaterialDialog(
                    dialogState = downloadDialog,
                    buttons = {
                        button(stringResource(id = R.string.screen_video_description_download_button_inapp)) {
                            if (!exist) {
                                val first = videoDetail.videoLinks.firstOrNull()
                                first?.let {
                                    context.downloadVideo(
                                        url = first.toLink(),
                                        videoDetail = videoDetail
                                    )
                                    Toast
                                        .makeText(
                                            context,
                                            context.stringResource(id = R.string.screen_video_description_download_button_inapp_add_queue),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    downloadDialog.hide()
                                } ?: kotlin.run {
                                    Toast.makeText(
                                        context,
                                        context.stringResource(id = R.string.screen_video_description_download_fail_resolve),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_video_description_download_complete),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        button(context.stringResource(id = R.string.screen_video_description_download_button_copy_link)) {
                            val first = videoDetail.videoLinks.firstOrNull()
                            first?.let {
                                context.setClipboard(first.toLink())
                            } ?: kotlin.run {
                                Toast.makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_video_description_download_fail_resolve),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            downloadDialog.hide()
                        }
                    }
                ) {
                    title(stringResource(id = R.string.screen_video_description_download_button_title))
                    message(stringResource(id = R.string.screen_video_description_download_button_message))
                }
                BottomNavigationItem(
                    selected = exist,
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = LocalContentColor.current.copy(ContentAlpha.medium),
                    onClick = {
                        downloadDialog.show()
                    },
                    icon = {
                        Icon(Icons.Default.Download, null)
                    },
                    label = {
                        Text(text = stringResource(id = R.string.screen_video_description_download_button_label))
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthorMoreVideo(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    // 更多视频
    Text(
        text = "${stringResource(id = R.string.screen_video_other_uploads)}:",
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )

    videoDetail.moreVideo.chunked(2).forEach {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            it.forEach {
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate("video/${it.id}")
                        }
                ) {
                    Column {
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
                            modifier = Modifier.padding(horizontal = 8.dp)
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
}