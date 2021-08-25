package com.rerere.iwara4a.ui.screen.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.model.history.HistoryData
import com.rerere.iwara4a.model.history.asString
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.util.format
import soup.compose.material.motion.MaterialFadeThrough

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            FullScreenTopBar(
                title = {
                    Text(text = "历史记录")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        historyViewModel.clearAll()
                    }) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier
            .navigationBarsPadding()
            .fillMaxSize()) {
            HistoryList(historyViewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryList(
    historyViewModel: HistoryViewModel
) {
    val historyList by historyViewModel.historyList.collectAsState(initial = emptyList())
    when {
        historyList.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(text = "暂无历史记录", fontWeight = FontWeight.Bold)
            }
        }
        else -> {
            LazyColumn {
                historyList.groupBy { it.date.format() }.forEach {
                    stickyHeader {
                        Surface(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                text = it.key,
                                fontWeight = FontWeight.Bold
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

@Composable
private fun HistoryItem(historyData: HistoryData){
    val navController = LocalNavController.current
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(historyData.route)
            },
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.4f)
                    .aspectRatio(16 / 9f),
                painter = rememberImagePainter(data = historyData.preview), 
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Column(Modifier.padding(8.dp)) {
                Text(text = historyData.title, fontWeight = FontWeight.Bold, maxLines = 1)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(text = historyData.date.format(detail = true))
                    Text(text = historyData.historyType.asString())
                }
            }
        }
    }
}