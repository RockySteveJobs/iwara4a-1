package com.rerere.iwara4a.ui.screen.video.tabs

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.alorma.compose.settings.storage.base.getValue
import com.alorma.compose.settings.storage.preferences.rememberPreferenceBooleanSettingState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.CommentItem
import com.rerere.iwara4a.ui.component.rememberReplyDialogState
import com.rerere.iwara4a.ui.screen.video.VideoViewModel
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.util.stringResource
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.title

@Composable
fun VideoScreenCommentTab(navController: NavController, videoViewModel: VideoViewModel) {
    val context = LocalContext.current
    val pager = videoViewModel.commentPager.collectAsLazyPagingItems()
    val state = rememberSwipeRefreshState(pager.loadState.refresh == LoadState.Loading)
    if (pager.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { pager.retry() }, contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                LottieAnimation(
                    modifier = Modifier.size(150.dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                Text(text = stringResource(id = R.string.load_error), fontWeight = FontWeight.Bold)
            }
        }
    } else {
        val dialog = rememberReplyDialogState()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            SwipeRefresh(
                modifier = Modifier
                    .fillMaxSize(),
                state = state,
                onRefresh = { pager.refresh() },
                indicator = { s, trigger ->
                    SwipeRefreshIndicator(s, trigger, contentColor = MaterialTheme.colorScheme.primary)
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    if (pager.itemCount == 0 && pager.loadState.refresh is LoadState.NotLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp), contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.screen_video_comment_nothing),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    items(pager) {
                        CommentItem(navController, it!!) { comment ->
                            dialog.open(
                                replyTo = comment.authorName,
                                nid = videoViewModel.videoDetailState.value.read().nid,
                                commentId = comment.commentId,
                                commentPostParam = videoViewModel.videoDetailState.value.read().commentPostParam
                            )
                        }
                    }

                    when (pager.loadState.append) {
                        LoadState.Loading -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Text(
                                        text = stringResource(id = R.string.loading),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .noRippleClickable { pager.retry() }
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.load_error),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier.padding(32.dp),
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

            val showCommentTail by rememberPreferenceBooleanSettingState(
                key = "setting.tail",
                defaultValue = true
            )
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
                                    pager.refresh()
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
                        }
                    )
                }
            }
        }
    }
}