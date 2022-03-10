package com.rerere.iwara4a.ui.screen.index.page

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.oreno3d.OrenoPreview
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.component.appendIndicator
import com.rerere.iwara4a.ui.component.items
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun RecommendPage(indexViewModel: IndexViewModel) {
    val pagerState = rememberPagerState(0)
    Column {
        Tab(pagerState = pagerState)
        HorizontalPager(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = pagerState,
            count = 4
        ) { page ->
            OrenoList(indexViewModel, indexViewModel.orenoList[page].second)
        }
    }
}

@Composable
private fun Tab(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage
    ) {
        listOf(
            stringResource(R.string.oreno3d_hot),
            stringResource(R.string.oreno3d_favorites),
            stringResource(R.string.oreno3d_latest),
            stringResource(R.string.oreno3d_popular)
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

@Composable
private fun OrenoList(indexViewModel: IndexViewModel, second: Flow<PagingData<OrenoPreview>>) {
    val previewList = second.collectAsLazyPagingItems()
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = previewList.loadState.refresh == LoadState.Loading
    )
    val listState = rememberLazyGridState()
    when (previewList.loadState.refresh) {
        is LoadState.Error -> {
            Text(
                text = stringResource(id = R.string.load_error),
                fontSize = 20.sp,
                modifier = Modifier.clickable {
                    previewList.refresh()
                })
        }
        else -> {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    previewList.refresh()
                }
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    state = listState
                ) {
                    items(
                        items = previewList
                    ) {
                        OrenoPreviewItem(indexViewModel, it!!)
                    }

                    appendIndicator(previewList)
                }
            }
        }
    }
}

@Composable
private fun OrenoPreviewItem(indexViewModel: IndexViewModel, mediaPreview: OrenoPreview) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    var loading by remember { mutableStateOf(false) }
    if (loading) {
        Dialog(
            onDismissRequest = {}
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "${stringResource(id = R.string.screen_index_oreno_parse_address)}...",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = {
            loading = true
            indexViewModel.openOrenoVideo(mediaPreview.id) {
                loading = false
                if (it.isNotBlank()) {
                    navController.navigate("video/$it")
                } else {
                    Toast
                        .makeText(
                            context,
                            context.stringResource(id = R.string.screen_index_oreno_not_iwara),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
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
                    Text(text = mediaPreview.watch, fontSize = 13.sp)
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(R.drawable.like_icon),
                        contentDescription = null
                    )
                    Text(text = mediaPreview.like, fontSize = 13.sp)
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.video),
                        fontSize = 13.sp,
                        textAlign = TextAlign.End
                    )
                }

                Text(text = mediaPreview.title, maxLines = 1, fontWeight = FontWeight.Medium)
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