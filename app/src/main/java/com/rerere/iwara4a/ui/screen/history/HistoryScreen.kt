package com.rerere.iwara4a.ui.screen.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rerere.iwara4a.R
import com.rerere.iwara4a.data.model.history.HistoryData
import com.rerere.iwara4a.data.model.history.asString
import com.rerere.iwara4a.ui.component.AppBarStyle
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.util.format

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            Md3TopBar(
                title = {
                    Text(text = stringResource(id = R.string.screen_history_topbar_title))
                },
                navigationIcon = {
                    BackIcon()
                },
                actions = {
                    IconButton(onClick = {
                        historyViewModel.clearAll()
                    }) {
                        Icon(Icons.Outlined.Delete, null)
                    }
                },
                appBarStyle = AppBarStyle.Large,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        HistoryList(historyViewModel, innerPadding)
    }
}

@Composable
private fun HistoryList(
    historyViewModel: HistoryViewModel,
    paddingValues: PaddingValues
) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val historyList by historyViewModel.historyList.collectAsState(initial = emptyList())
        when {
            historyList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_history_list_empty),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = WindowInsets.navigationBars.asPaddingValues()
                ) {
                    historyList.groupBy { it.date.format() }.forEach {
                        stickyHeader {
                            Surface(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    text = it.key,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        items(it.value) { history ->
                            HistoryItem(historyData = history)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(historyData: HistoryData) {
    val navController = LocalNavController.current
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(historyData.route)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.4f)
                    .aspectRatio(16 / 9f),
                model = historyData.preview,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Column(Modifier.padding(8.dp)) {
                Text(text = historyData.title, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = historyData.date.format(detail = true))
                Text(text = historyData.historyType.asString())
            }
        }
    }
}