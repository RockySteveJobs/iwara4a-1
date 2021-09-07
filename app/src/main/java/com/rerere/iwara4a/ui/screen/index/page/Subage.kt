package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.ui.public.ListSnapToTop
import com.rerere.iwara4a.ui.public.MediaPreviewCard
import com.rerere.iwara4a.ui.public.appendIndicator
import com.rerere.iwara4a.ui.public.items
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@Composable
fun SubPage(navController: NavController, indexViewModel: IndexViewModel) {
    val subscriptionList = indexViewModel.subscriptionPager.collectAsLazyPagingItems()
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = subscriptionList.loadState.refresh == LoadState.Loading)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            subscriptionList.loadState.refresh is LoadState.Error -> {
                SubPageError(subscriptionList)
            }
            subscriptionList.loadState.refresh == LoadState.Loading && subscriptionList.itemCount == 0 -> {
                SubPageLoading()
            }
            else -> {
                val listState = rememberLazyListState()
                ListSnapToTop(
                    listState = listState
                ) {
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = { subscriptionList.refresh() },
                        indicator = { s, trigger ->
                            SwipeRefreshIndicator(
                                s,
                                trigger,
                                contentColor = MaterialTheme.colors.primary
                            )
                        }
                    ) {
                        // TODO: 性能问题
                        // 似乎Paging3 + LazyVerticalGrid的性能极差
                        // 会导致闪屏和掉帧
                        // 由于官方未添加对grid的paging支持，所以暂时放着吧
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            cells = GridCells.Fixed(2),
                            state = listState
                        ) {
                            items(subscriptionList) {
                                MediaPreviewCard(navController, it!!)
                            }

                            this.appendIndicator(subscriptionList)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubPageError(subscriptionList: LazyPagingItems<MediaPreview>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                subscriptionList.retry()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
            LottieAnimation(
                modifier = Modifier.size(150.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
            Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SubPageLoading() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.hard_disk
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(200.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
    }
}