package com.rerere.iwara4a.ui.public

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingSource
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.DataState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BetterPager(
    loader: PagingSource<Int, MediaPreview>
) {
    val navController = LocalNavController.current
    var currentPage by rememberSaveable {
        mutableStateOf(0)
    }
    var hasNext by remember {
        mutableStateOf(false)
    }
    var items : DataState<List<MediaPreview>> by remember {
        mutableStateOf(
            DataState.Empty
        )
    }
    LaunchedEffect(currentPage){
        items = DataState.Loading
        val result = loader.load(PagingSource.LoadParams.Refresh(
            currentPage, 32, false
        ))
        if(result is PagingSource.LoadResult.Page){
            items = DataState.Success(result.data)
            hasNext = result.nextKey != null
        } else if(result is PagingSource.LoadResult.Error){
            items = DataState.Error("error")
        }
    }
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            IconButton(onClick = { currentPage = currentPage.minus(1).coerceAtLeast(0) }) {
                Icon(Icons.Default.ArrowBack, null)
            }
            Text(text = "Page: ${currentPage + 1}")
            IconButton(onClick = {
                if(hasNext) {
                    currentPage = currentPage.plus(1)
                }
            }) {
                Icon(Icons.Default.ArrowForward, null)
            }
        }
        Crossfade(targetState = items) {
            when (it) {
                is DataState.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        cells = GridCells.Fixed(2)
                    ) {
                        items(it.read()) {
                            MediaPreviewCard(navController, it)
                        }
                    }
                }
                is DataState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                        Text(text = "加载失败")
                    }
                }
            }
        }
    }
}