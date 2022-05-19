package com.rerere.iwara4a.ui.screen.video.tabs

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.component.SmartLinkText
import com.rerere.iwara4a.ui.component.md.ButtonStyle
import com.rerere.iwara4a.ui.component.md.ButtonX
import com.rerere.iwara4a.ui.component.rememberMaterialDialogState
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.ui.screen.video.VideoViewModel
import com.rerere.iwara4a.ui.states.rememberWindowDpSize
import com.rerere.iwara4a.util.downloadVideo
import com.rerere.iwara4a.util.setClipboard
import com.rerere.iwara4a.util.shareMedia
import com.rerere.iwara4a.util.stringResource
import soup.compose.material.motion.MaterialFadeThrough

@Composable
fun VideoScreenDetailTab(
    videoViewModel: VideoViewModel,
    videoDetail: VideoDetail
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        contentPadding = WindowInsets.navigationBars.asPaddingValues()
    ) {
        item {
            // 视频简介
            VideoDetail(videoDetail, videoViewModel)
        }

        item {
            // 作者更多视频
            MaterialFadeThrough(videoDetail) { detail ->
                when {
                    // Loaded
                    detail.moreVideo.isNotEmpty() -> AuthorMoreVideo(videoDetail)
                    // Shimmer Effect
                    detail.likeLink.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            repeat(3) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    repeat(2) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .weight(1f)
                                                .aspectRatio(16 / 9f)
                                                .placeholder(
                                                    visible = true,
                                                    highlight = PlaceholderHighlight.shimmer()
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoDetail(videoDetail: VideoDetail, videoViewModel: VideoViewModel) {
    var expand by rememberSaveable {
        mutableStateOf(false)
    }
    val screenSize = rememberWindowDpSize()
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 标题
                Text(
                    modifier = Modifier.weight(1f),
                    text = videoDetail.title,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = if (expand) 3 else 1
                )
                // 更多
                IconButton(onClick = { expand = !expand }) {
                    Icon(
                        if (!expand) Icons.Outlined.ExpandMore else Icons.Outlined.ExpandLess,
                        null
                    )
                }
            }

            // 视频信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "在 ${videoDetail.postDate} 上传",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall
                )
                Icon(
                    modifier = Modifier.size(17.dp),
                    painter = painterResource(R.drawable.play_icon),
                    contentDescription = null
                )
                Text(
                    text = videoDetail.watchs,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    modifier = Modifier.size(17.dp),
                    painter = painterResource(R.drawable.like_icon),
                    contentDescription = null
                )
                Text(
                    text = videoDetail.likes,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 介绍
            AnimatedVisibility(expand) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SelectionContainer {
                        SmartLinkText(
                            text = videoDetail.description,
                            maxLines = if (expand) Int.MAX_VALUE else 5,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 操作
            Actions(videoDetail, videoViewModel, expand)
        }
    }
}

@Composable
private fun ColumnScope.Actions(
    videoDetail: VideoDetail,
    videoViewModel: VideoViewModel,
    expand: Boolean
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val view = LocalView.current
    val windowSize = rememberWindowDpSize()
    val authorComp = remember {
        movableContentOf {
            // 作者头像
            AsyncImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .noRippleClickable {
                        navController.navigate("user/${videoDetail.authorId}")
                    },
                model = videoDetail.authorPic,
                contentDescription = null
            )

            // 作者名字
            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    modifier = Modifier
                        .noRippleClickable {
                            navController.navigate("user/${videoDetail.authorId}")
                        },
                    text = videoDetail.authorName,
                    maxLines = 1
                )
            }
        }
    }
    if (windowSize.width < 350.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            authorComp()
        }
    }

    // 操作
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (windowSize.width >= 350.dp) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                authorComp()
            }
        }

        // 关注
        ButtonX(
            style = if(videoDetail.follow) ButtonStyle.Outlined else ButtonStyle.Filled,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

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
        ) {
            Text(
                text = if (videoDetail.follow) stringResource(id = R.string.follow_status_following) else "+ ${
                    stringResource(
                        id = R.string.follow_status_not_following
                    )
                }"
            )
        }
        // 喜欢视频
        ButtonX(
            style = if(videoDetail.isLike) ButtonStyle.Outlined else ButtonStyle.Filled,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

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
            }
        ) {
            Icon(Icons.Outlined.Favorite, null)
            Text(
                text = if (videoDetail.isLike) {
                    stringResource(id = R.string.screen_video_description_like_status_liked)
                } else {
                    stringResource(
                        id = R.string.screen_video_description_like_status_no_like
                    )
                }
            )
        }
    }
    // 展开操作
    AnimatedVisibility(expand) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                videoViewModel.translate()
            }) {
                Icon(Icons.Outlined.Translate, null)
            }
            OutlinedButton(
                onClick = { navController.navigate("playlist?nid=${videoDetail.nid}") }
            ) {
                Text(text = stringResource(id = R.string.screen_video_description_playlist))
            }
            OutlinedButton(
                onClick = { context.shareMedia(MediaType.VIDEO, videoDetail.id) }
            ) {
                Text(text = stringResource(id = R.string.screen_video_description_share))
            }
            val downloadDialog = rememberMaterialDialogState()
            val exist by produceState(initialValue = false) {
                value = videoViewModel.database.getDownloadedVideoDao()
                    .getVideo(videoDetail.nid) != null
            }
            val videoLinks by videoViewModel.videoLink.collectAsState()
            if (downloadDialog.isVisible()) {
                AlertDialog(
                    onDismissRequest = { downloadDialog.hide() },
                    title = {
                        Text(stringResource(id = R.string.screen_video_description_download_button_title))
                    },
                    text = {
                        Text(stringResource(id = R.string.screen_video_description_download_button_message))
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (!exist) {
                                val first = videoLinks.readSafely()?.firstOrNull()
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
                        }) {
                            Text(stringResource(id = R.string.screen_video_description_download_button_inapp))
                        }
                        TextButton(onClick = {
                            val first = videoLinks.readSafely()?.firstOrNull()
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
                        }) {
                            Text(context.stringResource(id = R.string.screen_video_description_download_button_copy_link))
                        }
                    }
                )
            }
            OutlinedButton(
                onClick = { downloadDialog.show() }
            ) {
                Text(text = stringResource(id = R.string.screen_video_description_download_button_label))
            }
        }
    }
}

@Composable
private fun AuthorMoreVideo(videoDetail: VideoDetail) {
    val navController = LocalNavController.current
    Column {
        // 更多视频
        Text(
            text = "${stringResource(id = R.string.screen_video_other_uploads)}:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        videoDetail.moreVideo.chunked(2).fastForEach {
            Row {
                it.fastForEach {
                    Box(modifier = Modifier.weight(1f)) {
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
        }
    }
}