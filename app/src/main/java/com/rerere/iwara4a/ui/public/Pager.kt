package com.rerere.iwara4a.ui.public

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rerere.iwara4a.R
import com.rerere.iwara4a.util.DataState
import kotlinx.coroutines.flow.Flow

interface PageListProvider<T> {
    fun load(page: Int)

    fun getPage(): Flow<DataState<List<T>>>
}

@Composable
fun <T> PageList(
    state: PageListState,
    provider: PageListProvider<T>,
    sort: @Composable () -> Unit = {},
    item: @Composable (T) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                text = "第 ${state.page} 页"
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { state.prevPage() }) {
                    Icon(Icons.Rounded.ArrowLeft, null)
                }

                IconButton(onClick = { state.nextPage() }) {
                    Icon(Icons.Rounded.ArrowRight, null)
                }
            }
        }

        LaunchedEffect(state.page) {
            provider.load(state.page)
        }

        // Lazy Grid
        val data by provider.getPage().collectAsState(DataState.Empty)
        when (data) {
            is DataState.Success -> {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(),
                    cells = GridCells.Fixed(2),
                ) {
                    data.readSafely()?.let {
                        items(it) {
                            item(it)
                        }
                    }
                }
            }
            is DataState.Loading, DataState.Empty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fading_cubes_loader))
                    LottieAnimation(
                        modifier = Modifier.size(150.dp),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
            is DataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
                    LottieAnimation(
                        modifier = Modifier.size(150.dp),
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
        }
    }
}

@Composable
fun rememberPageListPage(initPage: Int = 1): PageListState =
    rememberSaveable(saver = PageListState.saver()) {
        PageListState(initPage)
    }

@Stable
class PageListState(
    initPage: Int
) {
    var page by mutableStateOf(initPage)

    fun jumpTo(page: Int) {
        this.page = page
    }

    fun prevPage() {
        if (page > 1) {
            page--
        }
    }

    fun nextPage() {
        page++
    }

    companion object {
        fun saver() = Saver<PageListState, Int>(
            save = {
                it.page
            },
            restore = {
                PageListState(it)
            }
        )
    }
}
