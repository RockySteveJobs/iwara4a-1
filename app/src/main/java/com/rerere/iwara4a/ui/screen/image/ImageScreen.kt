package com.rerere.iwara4a.ui.screen.image

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.data.model.detail.image.ImageDetail
import com.rerere.iwara4a.ui.component.*
import com.rerere.iwara4a.ui.component.basic.Centered
import com.rerere.iwara4a.ui.component.modifier.noRippleClickable
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.util.plus
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.downloadImageNew
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.launch
import me.rerere.zoomableimage.ZoomableImage

@Composable
fun ImageScreen(
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val imageDetail by imageViewModel.imageDetail.collectAsState()
    Scaffold(topBar = {
        Md3TopBar(
            title = {
                Text(
                    text = if (imageDetail is DataState.Success) imageDetail.read().title else stringResource(
                        id = R.string.screen_image_topbar_title
                    ),
                    maxLines = 1
                )
            },
            navigationIcon = {
                BackIcon()
            },
            actions = {
                if (imageDetail is DataState.Success) {
                    IconButton(onClick = {
                        imageDetail.readSafely()?.imageLinks?.forEachIndexed { i, link ->
                            context.downloadImageNew(
                                downloadUrlOfImage = link,
                                filename = "${imageViewModel.imageId}_$i"
                            )
                        }
                    }) {
                        Icon(Icons.Outlined.Download, null)
                    }
                }
            }
        )
    }) { padding ->
        when (imageDetail) {
            DataState.Empty, DataState.Loading -> {
                RandomLoadingAnim()
            }
            is DataState.Error -> {
                Centered(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                        LottieAnimation(
                            modifier = Modifier.size(150.dp),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                        Text(
                            text = stringResource(id = R.string.load_error),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            is DataState.Success -> {
                Box(modifier = Modifier.padding(padding)) {
                    ImagePage(navController, imageDetail.read(), imageViewModel)
                }
            }
        }
    }
}

@Composable
private fun ImagePage(
    navController: NavController,
    imageDetail: ImageDetail,
    imageViewModel: ImageViewModel
) {
    val pagerState = rememberPagerState(
        0
    )
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if (imageDetail.imageLinks.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                repeat(imageDetail.imageLinks.size) { page ->
                    Tab(
                        selected = pagerState.currentPage == page,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(page)
                            }
                        }) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Text(text = "${stringResource(id = R.string.screen_image_tab_text)} ${page + 1}")
                        }
                    }
                }
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black),
            state = pagerState,
            count = imageDetail.imageLinks.size
        ) { page ->
            val link = imageDetail.imageLinks[page]
            ZoomableImage(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                painter = rememberAsyncImagePainter(model = link)
            )
        }
        if (imageDetail.imageLinks.size > 1) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                    color = Color.White
                )
            }
        }
        var expand by remember {
            mutableStateOf(false)
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .noRippleClickable {
                            navController.navigate("user/${imageDetail.authorId}")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp),
                        model = imageDetail.authorProfilePic,
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = imageDetail.authorName,
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(onClick = { expand = !expand }) {
                        Icon(
                            if (expand) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                            null
                        )
                    }

                    val bottomSheetState = rememberBottomSheetState()
                    val scope = rememberCoroutineScope()
                    IconButton(
                        onClick = {
                            scope.launch {
                                bottomSheetState.peek()
                            }
                        }
                    ) {
                        Icon(Icons.Outlined.Comment, null)
                    }
                    BottomSheet(
                        state = bottomSheetState
                    ) {
                        ImageComment(imageViewModel) {
                            scope.launch {
                                bottomSheetState.collapse()
                            }
                        }
                    }
                }

                SelectionContainer {
                    SmartLinkText(
                        text = imageDetail.description.ifBlank {
                            "!!!∑(ﾟДﾟノ)ノ"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (expand) Int.MAX_VALUE else 3
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageComment(imageViewModel: ImageViewModel, onClose: () -> Unit) {
    val dialog = rememberReplyDialogState()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = {
                    onClose()
                }
            ) {
                Icon(Icons.Outlined.Close, null)
            }
            Text(
                text = stringResource(R.string.comment),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = {
                    dialog.open(
                        replyTo = context.stringResource(id = R.string.screen_video_comment_float_dialog_open),
                        nid = imageViewModel.imageDetail.value.read().nid,
                        commentId = null,
                        commentPostParam = imageViewModel.imageDetail.value.read().commentPostParam
                    )
                }
            ) {
                Text(stringResource(R.string.comment))
            }
        }

        val commentState by imageViewModel.commentPagerProvider.getPage()
            .collectAsState(DataState.Empty)
        PageList(
            state = rememberPageListPage(),
            provider = imageViewModel.commentPagerProvider
        ) { commentList ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(commentState is DataState.Loading),
                onRefresh = { imageViewModel.commentPagerProvider.refresh() }
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(8.dp) + WindowInsets.navigationBars.asPaddingValues()
                ) {
                    items(commentList) {
                        CommentItem(
                            comment = it,
                            onRequestTranslate = { text ->
                                imageViewModel.translate(text)
                            }
                        ) { comment ->
                            dialog.open(
                                replyTo = comment.authorName,
                                nid = imageViewModel.imageDetail.value.read().nid,
                                commentId = comment.commentId,
                                commentPostParam = imageViewModel.imageDetail.value.read().commentPostParam
                            )
                        }
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
                                imageViewModel.postReply(
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
                                    imageViewModel.commentPagerProvider.refresh()
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