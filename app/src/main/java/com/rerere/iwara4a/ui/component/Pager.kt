package com.rerere.iwara4a.ui.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rerere.iwara4a.R
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.flow.Flow
import soup.compose.material.motion.MaterialFadeThrough

interface PageListProvider<T> {
    fun load(page: Int, queryParam: MediaQueryParam?)

    fun getPage(): Flow<DataState<List<T>>>

    fun refresh() {}

    fun hasNext(): Boolean {
        return true
    }
}

@Composable
fun <T> PageList(
    modifier: Modifier = Modifier,
    state: PageListState,
    provider: PageListProvider<T>,
    supportQueryParam: Boolean = false,
    content: @Composable (List<T>) -> Unit
) {
    val context = LocalContext.current
    var page by remember {
        mutableStateOf(state.page.toString())
    }
    val data by provider.getPage().collectAsState(DataState.Empty)
    var showDialog by remember {
        mutableStateOf(false)
    }
    if(showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(stringResource(R.string.screen_index_subpage_move_title))
            },
            text = {
                OutlinedTextField(
                    value = page,
                    onValueChange = {
                        page = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        page.toIntOrNull()?.let {
                            state.jumpTo(it)
                        } ?: run {
                            Toast.makeText(
                                context,
                                context.stringResource(R.string.screen_index_subpage_over_zero),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm_button)
                    )
                }
            }
        )
    }

    Column(modifier.fillMaxSize()) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(supportQueryParam && data !is DataState.Empty) {
                QueryParamSelector(
                    queryParam = state.queryParam,
                    onChangeSort = {
                        state.queryParam = MediaQueryParam(
                            sortType = it,
                            filters = state.queryParam.filters
                        )
                    },
                    onChangeFiler = {
                        state.queryParam = MediaQueryParam(
                            state.queryParam.sortType,
                            it
                        )
                    },
                    onClose = {
                        provider.load(state.page, state.queryParam)
                    }
                )
            }

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { showDialog = true }
                    .weight(1f),
                text = stringResource(R.string.pager, state.page)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    enabled = state.page > 1,
                    onClick = {
                        if (data !is DataState.Empty) {
                            state.prevPage()
                        }
                    }
                ) {
                    Icon(Icons.Outlined.ArrowBack, null)
                }

                IconButton(
                    enabled = provider.hasNext(),
                    onClick = {
                        if (data !is DataState.Empty && provider.hasNext()) {
                            state.nextPage()
                        }
                    }
                ) {
                    Icon(Icons.Outlined.ArrowForward, null)
                }
            }
        }

        LaunchedEffect(state.page) {
            provider.load(state.page, if (supportQueryParam) state.queryParam else null)
        }

        // Lazy Grid
        MaterialFadeThrough(data) { data ->
            when (data) {
                is DataState.Success -> {
                    content(data.readSafely() ?: emptyList())
                }
                is DataState.Loading -> {
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
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                        LottieAnimation(
                            modifier = Modifier.size(150.dp),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    }
                }
                else -> {}
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
    initPage: Int,
    queryParam: MediaQueryParam = MediaQueryParam(SortType.DATE, hashSetOf())
) {
    var page by mutableStateOf(initPage)
    var queryParam by mutableStateOf(queryParam)

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
        @JvmStatic
        fun saver() = mapSaver(
            save = {
                mapOf(
                    "page" to it.page,
                    "sort" to it.queryParam.sortType,
                    "filter" to it.queryParam.filters
                )
            },
            restore = {
                PageListState(
                    initPage = it["page"] as Int,
                    queryParam = MediaQueryParam(
                        it["sort"] as SortType,
                        it["filter"] as MutableSet<String>
                    )
                )
            }
        )
    }
}
