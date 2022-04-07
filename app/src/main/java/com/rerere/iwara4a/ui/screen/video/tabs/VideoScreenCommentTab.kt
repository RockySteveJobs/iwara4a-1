package com.rerere.iwara4a.ui.screen.video.tabs

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.CommentItem
import com.rerere.iwara4a.ui.component.PageList
import com.rerere.iwara4a.ui.component.basic.LazyStaggeredGrid
import com.rerere.iwara4a.ui.component.rememberPageListPage
import com.rerere.iwara4a.ui.component.rememberReplyDialogState
import com.rerere.iwara4a.ui.screen.video.VideoViewModel
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.ui.util.plus
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.title

@Composable
fun VideoScreenCommentTab(navController: NavController, videoViewModel: VideoViewModel) {
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
                Icon(Icons.Default.Comment, null)
            }
        }
    ) {
        val commentState by videoViewModel.commentPagerProvider.getPage().collectAsState(DataState.Empty)
        PageList(
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
                        CommentItem(navController, it) { comment ->
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

        MaterialDialog(
            dialogState = dialog.materialDialog,
            buttons = {
                positiveButton(
                    if (dialog.posting) "${stringResource(id = R.string.screen_video_comment_submit_reply)}..." else stringResource(
                        id = R.string.screen_video_comment_submit
                    )
                ) {
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
                                    materialDialog.hide()
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
            }
        ) {
            title("${stringResource(id = R.string.screen_video_comment_reply)}: ${dialog.replyTo}")
            customView {
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
            }
        }
    }
}