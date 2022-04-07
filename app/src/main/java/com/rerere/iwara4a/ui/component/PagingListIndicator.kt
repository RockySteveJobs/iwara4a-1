package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.modifier.noRippleClickable

fun <T : Any> LazyGridScope.appendIndicator(pagingItems: LazyPagingItems<T>){
    when (pagingItems.loadState.append) {
        LoadState.Loading -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(Modifier.size(30.dp))
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "加载中..."
                    )
                }
            }
        }
        is LoadState.Error -> {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable { pagingItems.retry() }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .padding(10.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(R.drawable.anime_2),
                                contentDescription = null
                            )
                        }
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = "加载失败: ${(pagingItems.loadState.append as LoadState.Error).error.message}"
                        )
                        Text(text = "点击重试")
                    }
                }
            }
        }
        else -> {
        }
    }
}