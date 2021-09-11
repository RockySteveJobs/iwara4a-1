package com.rerere.iwara4a.ui.screen.index.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.*
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.oreno3d.OrenoPreview
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.ListSnapToTop
import com.rerere.iwara4a.ui.public.appendIndicator
import com.rerere.iwara4a.ui.public.items
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.ui.theme.uiBackGroundColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecommendPage(indexViewModel: IndexViewModel) {
    val pagerState = rememberPagerState(pageCount = 4)
    Column {
        Tab(pagerState = pagerState)
        HorizontalPager(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = pagerState
        ) { page ->
            OrenoList(indexViewModel, indexViewModel.orenoList[page].second)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Tab(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                color = MaterialTheme.colors.primary
            )
        },
        backgroundColor = MaterialTheme.colors.uiBackGroundColor
    ) {
        listOf(
            "快速上升",
            "高评价",
            "最新",
            "高人气"
        ).forEachIndexed { index, label ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                },
                text = {
                    Text(text = label)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrenoList(indexViewModel: IndexViewModel, second: Flow<PagingData<OrenoPreview>>) {
    val previewList = second.collectAsLazyPagingItems()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = previewList.loadState.refresh == LoadState.Loading
    )
    val listState = rememberLazyListState()
    when (previewList.loadState.refresh) {
        is LoadState.Error -> {
            Text(text = "加载失败，点击重试", fontSize = 20.sp, modifier = Modifier.clickable {
                previewList.refresh()
            })
        }
        else -> {
            ListSnapToTop(
                listState = listState
            ) {
                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        previewList.refresh()
                    }
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        cells = GridCells.Fixed(2),
                        state = listState
                    ) {
                        items(previewList) {
                            OrenoPreviewItem(indexViewModel, it!!)
                        }

                        appendIndicator(previewList)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
private fun OrenoPreviewItem(indexViewModel: IndexViewModel, mediaPreview: OrenoPreview) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    var loading by remember {
        mutableStateOf(false)
    }
    AnimatedVisibility(visible = loading) {
        Dialog(
            onDismissRequest = {}
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(30.dp))
                Text(text = "解析视频地址中...", fontSize = 20.sp, color = Color.White)
            }
        }
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                loading = true
                indexViewModel.openOrenoVideo(mediaPreview.id) {
                    loading = false
                    if (it.isNotBlank()) {
                        navController.navigate("video/$it")
                    } else {
                        Toast
                            .makeText(context, "该视频不是iwara视频，不支持打开！", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
        elevation = 2.dp
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp), contentAlignment = Alignment.BottomCenter
            ) {
                val coilPainter = rememberImagePainter(
                    data = mediaPreview.pic
                )
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .placeholder(
                            visible = coilPainter.state is ImagePainter.State.Empty || coilPainter.state is ImagePainter.State.Loading,
                            highlight = PlaceholderHighlight.shimmer()
                        ),
                    painter = coilPainter,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
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
                        Text(text = mediaPreview.watch, fontSize = 13.sp)
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
                        Text(text = mediaPreview.like, fontSize = 13.sp)
                    }

                    Row(modifier = Modifier.constrainAs(type) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "图片", fontSize = 13.sp
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