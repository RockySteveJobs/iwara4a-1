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
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.BuildConfig
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.SimpleIwaraTopBar
import com.rerere.iwara4a.util.stringResource
import com.rerere.iwara4a.util.toFileSize
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
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
        SimpleIwaraTopBar(navController, stringResource(id = R.string.screen_download_topbar_title), 0.dp)
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                DownloadedVideos(
                    navController = navController,
                    videoViewModel = downloadViewModel
                )
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
    val deleteDialog = rememberMaterialDialogState()
    val coroutineScope = rememberCoroutineScope()

    MaterialDialog(
        dialogState = deleteDialog,
        buttons = {
            positiveButton(stringResource(id = (R.string.yes_button))) {
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
                    Toast.makeText(context, context.stringResource(id = R.string.screen_download_item_delete), Toast.LENGTH_SHORT).show()
                }
            }
            negativeButton(stringResource(id = R.string.cancel_button)) {
                deleteDialog.hide()
            }
        }
    ) {
        title(stringResource(id = R.string.screen_download_item_title))
        message("${stringResource(id = R.string.screen_download_item_message)} ${downloadedVideo.title}")
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