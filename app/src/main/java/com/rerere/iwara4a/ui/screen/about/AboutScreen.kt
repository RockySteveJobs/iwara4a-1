package com.rerere.iwara4a.ui.screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rerere.iwara4a.R
import com.rerere.iwara4a.ui.public.DefTopBar

@Composable
fun AboutScreen(navController: NavController){
    Scaffold(
        topBar = {
            DefTopBar(navController, "关于")
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .padding(16.dp)) {
            Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold, fontSize = 30.sp)

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
            Text("EventBus (事件总线)")
            Text("Hilt (依赖注入)")
            Text("Paging3 (分页加载)")
        }
    }
}