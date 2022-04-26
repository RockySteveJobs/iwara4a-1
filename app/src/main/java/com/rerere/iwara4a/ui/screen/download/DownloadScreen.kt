package com.rerere.iwara4a.ui.screen.download

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.util.FileSize
import com.rerere.iwara4a.util.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DownloadScreen"

@Composable
fun DownloadScreen(
    navController: NavController,
    downloadViewModel: DownloadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
    Scaffold(topBar = {
        SimpleIwaraTopBar(stringResource(id = R.string.screen_download_topbar_title))
    }) {
        Column(Modifier.padding(it)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Outlined.Folder, null)
                    Text(fileDir?.absolutePath ?: "Null")
                }
            }
            DownloadedVideos(
                videoViewModel = downloadViewModel
            )
        }
    }
}

@Composable
private fun DownloadedVideos(videoViewModel: DownloadViewModel) {
    val list by videoViewModel.dao.getAllDownloadedVideos().collectAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.navigationBars.asPaddingValues()
    ) {
        items(list) {
            DownloadedVideoItem(downloadedVideo = it, downloadViewModel = videoViewModel)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun DownloadedVideoItem(
    downloadedVideo: DownloadedVideo,
    downloadViewModel: DownloadViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember {
        mutableStateOf(false)
    }
    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(stringResource(id = R.string.screen_download_item_title))
            },
            text = {
                Text("${stringResource(id = R.string.screen_download_item_message)} ${downloadedVideo.title}")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            withContext(Dispatchers.IO) {
                                // 删除数据库记录
                                downloadViewModel.database.getDownloadedVideoDao().delete(downloadedVideo)

                                // 删除视频文件
                                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.let { folder ->
                                    File(folder, downloadedVideo.fileName).takeIf { it.exists() }?.delete()
                                }
                            }
                            Toast.makeText(
                                context,
                                context.stringResource(id = R.string.screen_download_item_delete),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text(stringResource(R.string.yes_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }
    ElevatedCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(80.dp)
            .combinedClickable(
                onLongClick = {
                    showDialog = true
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
                                    } ?: run {
                                    Toast
                                        .makeText(
                                            context,
                                            context.stringResource(id = R.string.screen_download_item_load_failed),
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val painter = rememberAsyncImagePainter(downloadedVideo.preview)
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16 / 9f)
                    .placeholder(
                        visible = painter.state is AsyncImagePainter.State.Loading,
                        highlight = PlaceholderHighlight.shimmer()
                    ),
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(text = downloadedVideo.title, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(
                    text = "${SimpleDateFormat("yyyy/MM/dd").format(Date(downloadedVideo.downloadDate))} - ${
                        FileSize(
                            downloadedVideo.size
                        )
                    }",
                    fontSize = 13.sp
                )
            }
        }
    }
}