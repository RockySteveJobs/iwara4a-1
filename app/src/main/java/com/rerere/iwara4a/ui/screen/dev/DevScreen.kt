package com.rerere.iwara4a.ui.screen.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsPadding
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.DefTopBar
import com.rerere.iwara4a.ui.public.FullScreenTopBar
import com.rerere.iwara4a.ui.theme.PINK
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevScreen() {
    val navController = LocalNavController.current
    val sheetState = rememberBottomSheetScaffoldState()
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
            DefTopBar(navController = navController, title = "测试")
        },
        scaffoldState = sheetState
    ) {
        LaunchedEffect(sheetState.bottomSheetState.progress){
            println(sheetState.bottomSheetState.progress.fraction)
        }
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .blur((sheetState.currentFraction * 5f).dp)
            .graphicsLayer {
                scaleX = 1 - (sheetState.currentFraction * 0.1f)
                scaleY = 1 - (sheetState.currentFraction * 0.1f)
            }
        ) {
            repeat(100) {
                Text(text = "测试", fontSize = 15.sp)
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