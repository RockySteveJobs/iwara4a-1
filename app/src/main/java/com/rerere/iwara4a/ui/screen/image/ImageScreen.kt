package com.rerere.iwara4a.ui.screen.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.rerere.iwara4a.R
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.public.ImageViewer

@ExperimentalPagerApi
@Composable
fun ImageScreen(
    navController: NavController,
    imageId: String,
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        imageViewModel.load(imageId)
    }
    Scaffold(topBar = {
        FullScreenTopBar(
            title = {
                Text(text = if (imageViewModel.imageDetail != ImageDetail.LOADING && !imageViewModel.isLoading && !imageViewModel.error) imageViewModel.imageDetail.title else "浏览图片")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        )
    }) {
        if (imageViewModel.error) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error_state_dog))
                    LottieAnimation(modifier = Modifier.size(150.dp), composition = composition, iterations = LottieConstants.IterateForever)
                    Text(text = "加载失败，点击重试", fontWeight = FontWeight.Bold)
                }
            }
        } else if (imageViewModel.isLoading || imageViewModel.imageDetail == ImageDetail.LOADING) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.paperplane))
                    LottieAnimation(modifier = Modifier.size(170.dp), composition = composition, iterations = LottieConstants.IterateForever)
                    Text(text = "加载中", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            ImagePage(imageViewModel.imageDetail)
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun ImagePage(imageDetail: ImageDetail) {
    val pagerState = rememberPagerState(pageCount = imageDetail.imageLinks.size, initialPage = 0, initialOffscreenLimit = 5)
    Column(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black), state = pagerState
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ImageViewer(modifier = Modifier.fillMaxSize(), link = imageDetail.imageLinks[pagerState.currentPage])
            }
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = rememberImagePainter(imageDetail.authorProfilePic),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = imageDetail.authorId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
            }
        }
    }
}