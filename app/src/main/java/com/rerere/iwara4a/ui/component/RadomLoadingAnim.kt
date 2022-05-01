package com.rerere.iwara4a.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.component.basic.Centered

private val animList = listOf(
    R.raw.niko,
    R.raw.loading_anim_2
)

@Composable
fun RandomLoadingAnim() {
    Centered(
        modifier = Modifier.fillMaxSize()
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                animList.random()
            )
        )
        LottieAnimation(
            modifier = Modifier.size(200.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever
        )
    }
}