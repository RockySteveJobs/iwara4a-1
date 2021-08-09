package com.rerere.iwara4a.ui.screen.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.DefTopBar

@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            DefTopBar(navController, "关于")
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hanatachi))
            LottieAnimation(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .size(100.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "介绍", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "基于Jetpack Compose开发的 iwara 安卓app, 采用Material Design, 支持夜间模式, 支持绝大多数iwara网站上的功能。")

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "开源地址", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "https://github.com/jiangdashao/iwara4a")

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "技术栈", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("MVVM 架构")
            Text("Jetpack Compose (构建UI)")
            Text("Kotlin Coroutine (协程)")
            Text("Okhttp + Jsoup (解析网页)")
            Text("Retrofit (访问Restful API)")
            Text("Navigation Compose (导航)")
            Text("Hilt (依赖注入)")
            Text("Paging3 (分页加载)")
        }
    }
}
