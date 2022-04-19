package com.rerere.iwara4a.ui.screen.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.ui.component.BackIcon
import com.rerere.iwara4a.ui.component.Md3TopBar
import com.rerere.iwara4a.ui.component.SmartLinkText
import com.rerere.iwara4a.ui.modifier.noRippleClickable
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.downloadImageNew
import kotlinx.coroutines.launch
import me.rerere.zoomableimage.ZoomableImage

@Composable
fun ImageScreen(
    navController: NavController,
    imageId: String,
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        imageViewModel.load(imageId)
    }
    val imageDetail by imageViewModel.imageDetail.collectAsState()
    Scaffold(topBar = {
        Md3TopBar(
            title = {
                Text(
                    text = if (imageDetail is DataState.Success) imageDetail.read().title else stringResource(
                        id = R.string.screen_image_topbar_title
                    ),
                    maxLines = 1
                )
            },
            navigationIcon = {
                BackIcon()
            },
            actions = {
                if (imageDetail is DataState.Success) {
                    IconButton(onClick = {
                        imageDetail.readSafely()?.imageLinks?.forEachIndexed { i, link ->
                            context.downloadImageNew(
                                downloadUrlOfImage = link,
                                filename = "${imageId}_$i"
                            )
                        }
                    }) {
                        Icon(Icons.Outlined.Download, null)
                    }
                }
            }
        )
    }) {
        when (imageDetail) {
            DataState.Empty,
            DataState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.niko))
                        LottieAnimation(
                            modifier = Modifier.size(170.dp),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    }
                }
            }
            is DataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
                        LottieAnimation(
                            modifier = Modifier.size(150.dp),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                        Text(
                            text = stringResource(id = R.string.load_error),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            is DataState.Success -> {
                ImagePage(navController, imageDetail.read())
            }
        }
    }
}

@Composable
private fun ImagePage(navController: NavController, imageDetail: ImageDetail) {
    val pagerState = rememberPagerState(
        0
    )
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if (imageDetail.imageLinks.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                repeat(imageDetail.imageLinks.size) { page ->
                    Tab(
                        selected = pagerState.currentPage == page,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(page)
                            }
                        }) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Text(text = "${stringResource(id = R.string.screen_image_tab_text)} ${page + 1}")
                        }
                    }
                }
            }
        }
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black),
            state = pagerState,
            count = imageDetail.imageLinks.size
        ) { page ->
            val link = imageDetail.imageLinks[page]
            ZoomableImage(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                painter = rememberAsyncImagePainter(model = link)
            )
        }
        if (imageDetail.imageLinks.size > 1) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                    color = Color.White
                )
            }
        }
        var expand by remember {
            mutableStateOf(false)
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .noRippleClickable {
                            navController.navigate("user/${imageDetail.authorId}")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(50.dp),
                        model = imageDetail.authorProfilePic,
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = imageDetail.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )

                    IconButton(onClick = { expand = !expand }) {
                        Icon(if(expand) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore, null)
                    }
                }

                SelectionContainer {
                    SmartLinkText(
                        text = imageDetail.description.ifBlank {
                            "!!!∑(ﾟДﾟノ)ノ"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if(expand) Int.MAX_VALUE else 3
                    )
                }
            }
        }
    }
}