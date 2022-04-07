package com.rerere.iwara4a.ui.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
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
    val jumpDialog = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = jumpDialog,
        buttons = {
            positiveButton(stringResource(R.string.confirm_button)) {
                page.toIntOrNull()?.let {
                    state.jumpTo(it)
                } ?: kotlin.run {
                    Toast.makeText(
                        context,
                        context.stringResource(R.string.screen_index_subpage_over_zero),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                jumpDialog.hide()
            }
        }
    ) {
        title(stringResource(R.string.screen_index_subpage_move_title))
        customView {
            LaunchedEffect(state.page) {
                page = state.page.toString()
            }
            OutlinedTextField(
                value = page,
                onValueChange = {
                    page = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }
    }


    Column(Modifier.fillMaxSize()) {
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
                    .clickable { jumpDialog.show() }
                    .weight(1f),
                text = stringResource(R.string.pager, state.page)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (data !is DataState.Empty) {
                        state.prevPage()
                    }
                }) {
                    Icon(Icons.Rounded.ArrowBack, null)
                }

                IconButton(onClick = {
                    if (data !is DataState.Empty && provider.hasNext()) {
                        state.nextPage()
                    }
                }) {
                    Icon(Icons.Rounded.ArrowForward, null)
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
