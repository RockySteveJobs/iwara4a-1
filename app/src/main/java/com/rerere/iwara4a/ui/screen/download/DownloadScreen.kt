package com.rerere.iwara4a.ui.screen.download

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.util.toFileSize
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DownloadScreen"

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun DownloadScreen(
    navController: NavController,
    downloadViewModel: DownloadViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val pager = rememberPagerState(0)
    Scaffold(topBar = {
        DefTopBar(navController, "缓存", 0.dp)
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()) {
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
                    text = {
                        Text("正在下载")
                    },
                    selected = pager.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pager.animateScrollToPage(1)
                        }
                    },
                )
            }
            HorizontalPager(modifier = Modifier.fillMaxSize(), state = pager, count = 2) { page ->
                when (page) {
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
private fun DownloadedVideos(navController: NavController, videoViewModel: DownloadViewModel) {
    val list by videoViewModel.dao.getAllDownloadedVideos().collectAsState(initial = emptyList())
    LazyColumn(Modifier.fillMaxSize()) {
        items(list) {
            DownloadedVideoItem(downloadedVideo = it)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
private fun DownloadedVideoItem(downloadedVideo: DownloadedVideo) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val deleteDialog = remember {
        MaterialDialog()
    }
    val coroutineScope = rememberCoroutineScope()

    deleteDialog.build(
        buttons = {
            positiveButton("是的") {
                deleteDialog.hide()
                coroutineScope.launch {
                    withContext(Dispatchers.IO) {
                        // 删除数据库记录
                        AppContext.database.getDownloadedVideoDao().delete(downloadedVideo)

                        // 删除视频文件
                        context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.let { folder ->
                            File(folder, downloadedVideo.fileName).takeIf { it.exists() }?.delete()
                        }
                    }
                    Toast.makeText(context, "已删除该视频缓存！", Toast.LENGTH_SHORT).show()
                }
            }
            negativeButton("取消") {
                deleteDialog.hide()
            }
        }
    ) {
        title("是否删除这个视频缓存?")
        message("视频名: ${downloadedVideo.title}")
    }
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onLongClick = {
                    deleteDialog.show()
                },
                onClick = {
                    try {
                        context
                            .getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                            ?.let { folder ->
                                File(folder, downloadedVideo.fileName)
                                    .takeIf { it.exists() }
                                    ?.let { file ->
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            BuildConfig.APPLICATION_ID + ".provider",
                                            file
                                        )
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, "video/*")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(intent)
                                        Log.i(
                                            TAG,
                                            "DownloadedVideoItem: Open downloaded video: ${downloadedVideo.title}"
                                        )
                                    } ?: kotlin.run {
                                    Toast
                                        .makeText(
                                            context,
                                            "视频文件已丢失！请尝试重新下载！",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            ),
        elevation = 2.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16 / 9f),
                painter = rememberImagePainter(downloadedVideo.preview),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(text = downloadedVideo.title, fontWeight = FontWeight.Bold)
                Text(
                    text = "${SimpleDateFormat("yyyy/MM/dd").format(Date(downloadedVideo.downloadDate))} - ${downloadedVideo.size.toFileSize()}",
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun DownloadingVideos(navController: NavController, videoViewModel: DownloadViewModel) {
    val list by videoViewModel.downloading.collectAsState(initial = emptyList())
    LazyColumn(Modifier.fillMaxSize()) {
        items(list) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(80.dp),
                elevation = 2.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(16 / 9f),
                        painter = rememberImagePainter(it.preview),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight
                    )
                    Column(Modifier.padding(16.dp)) {
                        Text(text = it.title, fontWeight = FontWeight.Bold, maxLines = 1)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                modifier = Modifier.weight(1f),
                                progress = it.progress
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${(it.progress * 100f).toInt().coerceIn(0..100)}%")
                        }
                    }
                }
            }
        }
    }
}