package com.rerere.iwara4a.ui.screen.index.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.component.PageList
import com.rerere.iwara4a.ui.component.rememberPageListPage
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.screen.index.IndexViewModel
import kotlinx.coroutines.launch

@Composable
fun ExplorePage(indexViewModel: IndexViewModel) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage
        ) {
            Tab(
                selected = pagerState.currentPage == 0,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(0)
                    }
                },
                text = {
                    Text(stringResource(R.string.screen_index_bottom_video))
                }
            )
            Tab(
                selected = pagerState.currentPage == 1,
                onClick = {
                    scope.launch {
                        pagerState.scrollToPage(1)
                    }
                },
                text = {
                    Text(stringResource(R.string.screen_index_bottom_image))
                }
            )
        }
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            count = 2
        ) {
            when (it) {
                0 -> {
                    VideoListPage(indexViewModel)
                }
                1 -> {
                    ImageListPage(indexViewModel)
                }
            }
        }
    }
}

@Composable
fun VideoListPage(indexViewModel: IndexViewModel) {
    val pageListState = rememberPageListPage()
    PageList(
        state = pageListState,
        provider = indexViewModel.videoListPrvider,
        supportQueryParam = true
    ) {
        MediaPreviewCard(LocalNavController.current, it)
    }
}

@Composable
fun ImageListPage(indexViewModel: IndexViewModel) {
    val pageListState = rememberPageListPage()
    PageList(
        state = pageListState,
        provider = indexViewModel.imageListProvider,
        supportQueryParam = true
    ) {
        MediaPreviewCard(LocalNavController.current, it)
    }
}