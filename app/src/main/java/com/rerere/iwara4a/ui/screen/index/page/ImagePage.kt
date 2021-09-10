package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.*
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@Composable
fun ImageListPage(navController: NavController, indexViewModel: IndexViewModel) {
    val imageList = indexViewModel.imagePager.collectAsLazyPagingItems()
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = imageList.loadState.refresh == LoadState.Loading)

    if (imageList.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable {
                    imageList.retry()
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
    } else {
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { imageList.refresh() },
            indicator = { s, trigger ->
                SwipeRefreshIndicator(s, trigger, contentColor = MaterialTheme.colors.primary)
            }) {
            Column {
                QueryParamSelector(
                    queryParam = indexViewModel.imageQueryParam,
                    onChangeSort = {
                        indexViewModel.imageQueryParam.sortType = it
                        imageList.refresh()
                    },
                    onChangeFilters = {
                        indexViewModel.imageQueryParam.filters = it
                        imageList.refresh()
                    }
                )
                val listState = rememberLazyListState()
                Box(contentAlignment = Alignment.Center) {
                    ListSnapToTop(
                        listState = listState
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            cells = GridCells.Fixed(2),
                            state = listState
                        ) {
                            items(imageList) {
                                MediaPreviewCard(navController, it!!)
                            }

                            appendIndicator(imageList)
                        }
                    }
                    if (imageList.loadState.refresh == LoadState.Loading && imageList.itemCount == 0) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.cola_can
                            )
                        )
                        LottieAnimation(
                            modifier = Modifier.size(250.dp),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    }
                }
            }
        }
    }
}