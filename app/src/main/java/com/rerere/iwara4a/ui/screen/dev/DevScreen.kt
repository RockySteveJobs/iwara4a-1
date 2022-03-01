package com.rerere.iwara4a.ui.screen.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.component.MediaPreviewCard
import com.rerere.iwara4a.ui.component.SimpleIwaraTopBar
import com.rerere.iwara4a.ui.component.items

@OptIn(ExperimentalMaterialApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DevScreen(devViewmodel: DevViewmodel = hiltViewModel()) {
    val navController = LocalNavController.current
    val sheetState = rememberBottomSheetScaffoldState()
    val subList = devViewmodel.subscriptionPager.collectAsLazyPagingItems()
    BottomSheetScaffold(
        modifier = Modifier.navigationBarsPadding(),
        sheetContent = {
            Box(modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .fillMaxWidth()
                .height(300.dp)
                .background(MaterialTheme.colors.primary)
            )
        },
        topBar = {
            SimpleIwaraTopBar("测试")
        },
        scaffoldState = sheetState
    ) {
        LazyVerticalGrid(cells = GridCells.Fixed(2),modifier = Modifier.fillMaxSize()){
            items(subList){
                MediaPreviewCard(navController = navController, mediaPreview = it!!)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
val BottomSheetScaffoldState.currentFraction: Float
    get() {
        val fraction = bottomSheetState.progress.fraction
        val targetValue = bottomSheetState.targetValue
        val currentValue = bottomSheetState.currentValue

        return when {
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Collapsed -> 0f
            currentValue == BottomSheetValue.Expanded && targetValue == BottomSheetValue.Expanded -> 1f
            currentValue == BottomSheetValue.Collapsed && targetValue == BottomSheetValue.Expanded -> fraction
            else -> 1f - fraction
        }
    }