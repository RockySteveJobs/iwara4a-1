package com.rerere.iwara4a.ui.screen.search

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.*
import com.rerere.iwara4a.util.noRippleClickable

@ExperimentalFoundationApi
@Composable
fun SearchScreen(navController: NavController, searchViewModel: SearchViewModel = hiltViewModel()) {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            IwaraTopBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                title = {
                    Text(text = stringResource(R.string.search))
                }
            )
        }
    ) {
        val result = searchViewModel.pager.collectAsLazyPagingItems()

        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            SearchBar(searchViewModel, result)
            Result(navController, searchViewModel, result)
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun Result(
    navController: NavController,
    searchViewModel: SearchViewModel,
    list: LazyPagingItems<MediaPreview>
) {
    if (list.loadState.refresh !is LoadState.Error) {
        Crossfade(searchViewModel.query) {
            if (it.isNotBlank()) {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(list.loadState.refresh == LoadState.Loading),
                    onRefresh = { list.refresh() },
                    indicator = { s, trigger ->
                        SwipeRefreshIndicator(
                            s,
                            trigger,
                            contentColor = MaterialTheme.colors.primary
                        )
                    }
                ) {
                    Column {
                        QueryParamSelector(
                            queryParam = searchViewModel.searchParam,
                            onChangeSort = {
                                searchViewModel.searchParam.sortType = it
                                list.refresh()
                            },
                            onChangeFilters = {
                                searchViewModel.searchParam.filters = it
                                list.refresh()
                            }
                        )
                        Box {
                            LazyVerticalGrid(
                                modifier = Modifier.fillMaxSize(),
                                cells = GridCells.Fixed(2)
                            ) {
                                items(list) {
                                    MediaPreviewCard(navController, it!!)
                                }

                                when (list.loadState.append) {
                                    LoadState.Loading -> {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                CircularProgressIndicator()
                                                Text(text = "加载中")
                                            }
                                        }
                                    }
                                    is LoadState.Error -> {
                                        item {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .noRippleClickable { list.retry() }
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "加载失败，点击重试",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            if (list.loadState.refresh == LoadState.Loading && list.itemCount == 0) {
                                val composition by rememberLottieComposition(
                                    LottieCompositionSpec.RawRes(
                                        R.raw.dolphin
                                    )
                                )
                                LottieAnimation(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .align(Alignment.Center),
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever
                                )
                            }
                        }
                    }
                }
            } else {
                // 也许可以加个搜索推荐？
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { list.refresh() }, contentAlignment = Alignment.Center
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
}

@Composable
private fun SearchBar(searchViewModel: SearchViewModel, list: LazyPagingItems<MediaPreview>) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Card(modifier = Modifier.padding(8.dp), elevation = 4.dp, shape = RoundedCornerShape(6.dp)) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = searchViewModel.query,
                    onValueChange = { searchViewModel.query = it.replace("\n", "") },
                    maxLines = 1,
                    placeholder = {
                        Text(text = "搜索视频和图片")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    trailingIcon = {
                        if (searchViewModel.query.isNotEmpty()) {
                            IconButton(onClick = { searchViewModel.query = "" }) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchViewModel.query.isBlank()) {
                                Toast.makeText(context, "不能搜索空内容哦！", Toast.LENGTH_SHORT).show()
                            } else {
                                focusManager.clearFocus()
                                list.refresh()
                            }
                        }
                    )
                )
            }
            IconButton(onClick = {
                if (searchViewModel.query.isBlank()) {
                    Toast.makeText(context, "不能搜索空内容哦！", Toast.LENGTH_SHORT).show()
                } else {
                    focusManager.clearFocus()
                    list.refresh()
                }
            }) {
                Icon(Icons.Default.Search, null)
            }
        }
    }
}