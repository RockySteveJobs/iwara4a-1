package com.rerere.iwara4a.ui.screen.video.tabs

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.CommentItem
import com.rerere.iwara4a.ui.component.PageList
import com.rerere.iwara4a.ui.component.rememberPageListPage
import com.rerere.iwara4a.ui.component.rememberReplyDialogState
import com.rerere.iwara4a.ui.screen.video.VideoViewModel
import com.rerere.iwara4a.ui.util.plus
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource

@Composable
fun VideoScreenCommentTab(videoViewModel: VideoViewModel) {
    val context = LocalContext.current
    val dialog = rememberReplyDialogState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = {
                    dialog.open(
                        replyTo = context.stringResource(id = R.string.screen_video_comment_float_dialog_open),
                        nid = videoViewModel.videoDetailState.value.read().nid,
                        commentId = null,
                        commentPostParam = videoViewModel.videoDetailState.value.read().commentPostParam
                    )
                }
            ) {
                Icon(Icons.Outlined.Comment, null)
            }
        }
    ) { padding ->
        val commentState by videoViewModel.commentPagerProvider.getPage()
            .collectAsState(DataState.Empty)
        PageList(
            modifier = Modifier.padding(padding),
            state = rememberPageListPage(),
            provider = videoViewModel.commentPagerProvider
        ) { commentList ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(commentState is DataState.Loading),
                onRefresh = { videoViewModel.commentPagerProvider.refresh() }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp) + WindowInsets.navigationBars.asPaddingValues()
                ) {
                    items(commentList) {
                        CommentItem(
                            comment = it,
                            onRequestTranslate = { text ->
                                videoViewModel.translate(text)
                            }
                        ) { comment ->
                            dialog.open(
                                replyTo = comment.authorName,
                                nid = videoViewModel.videoDetailState.value.read().nid,
                                commentId = comment.commentId,
                                commentPostParam = videoViewModel.videoDetailState.value.read().commentPostParam
                            )
                        }
                    }
                }
            }
        }

        if (dialog.showDialog) {
            AlertDialog(
                onDismissRequest = {
                    dialog.showDialog = false
                },
                title = {
                    Text("${stringResource(id = R.string.screen_video_comment_reply)}: ${dialog.replyTo}")
                },
                text = {
                    OutlinedTextField(
                        value = dialog.content,
                        onValueChange = { dialog.content = it },
                        label = {
                            Text(text = stringResource(id = R.string.screen_video_comment_label))
                        },
                        placeholder = {
                            Text(text = stringResource(id = R.string.screen_video_comment_placeholder))
                        },
                        modifier = Modifier
                            .height(100.dp)
                            .imePadding()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (dialog.content.isNotEmpty()) {
                                if (!dialog.posting) {
                                    dialog.posting = true
                                    videoViewModel.postReply(
                                        content = dialog.content,
                                        nid = dialog.nid,
                                        commentId = if (dialog.commentId == -1) null else dialog.commentId,
                                        commentPostParam = dialog.commentPostParam
                                    ) {
                                        dialog.apply {
                                            posting = false
                                            dialog.showDialog = false
                                            content = ""
                                        }
                                        Toast.makeText(
                                            context,
                                            context.stringResource(id = R.string.screen_video_comment_reply_success),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        videoViewModel.commentPagerProvider.refresh()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.stringResource(id = R.string.screen_video_comment_reply_not_empty),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text(
                            text = if (dialog.posting) "${stringResource(id = R.string.screen_video_comment_submit_reply)}..." else stringResource(
                                id = R.string.screen_video_comment_submit
                            )
                        )
                    }
                }
            )
        }
    }
}