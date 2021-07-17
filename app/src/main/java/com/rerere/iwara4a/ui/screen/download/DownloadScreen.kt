package com.rerere.iwara4a.ui.screen.download

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun DownloadScreen(
    navController: NavController,
    downloadViewModel: DownloadViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val pager = rememberPagerState(pageCount = 2, initialPage = 0)
    Scaffold(topBar = {
        FullScreenTopBar(
            title = {
                Text(text = "缓存的视频")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        )
    }) {
        Column(Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = pager.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pager, tabPositions)
                    )
                },
                backgroundColor = MaterialTheme.colors.background
            ) {
                Tab(
                    text = { Text("已缓存") },
                    selected = pager.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pager.animateScrollToPage(0)
                        }
                    },
                )
                Tab(
                    text = { Text("正在下载") },
                    selected = pager.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pager.animateScrollToPage(1)
                        }
                    },
                )
            }
            HorizontalPager(modifier = Modifier.fillMaxSize(), state = pager) { page ->
                when(page){
                    0 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            DownloadedVideos(
                                navController = navController,
                                videoViewModel = downloadViewModel
                            )
                        }
                    }
                    1 -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            DownloadingVideos(
                                navController = navController,
                                videoViewModel = downloadViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadedVideos(navController: NavController, videoViewModel: DownloadViewModel){

}

@Composable
private fun DownloadingVideos(navController: NavController, videoViewModel: DownloadViewModel){

}