package com.rerere.iwara4a.ui.screen.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rerere.iwara4a.R

@Composable
fun SplashScreen(navController: NavController, splashViewModel: SplashViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .animateContentSize()
                .wrapContentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    model = R.drawable.miku,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "Iwara",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "ecchi.iwara.tv",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            AnimatedVisibility(splashViewModel.checkingCookkie) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.width(150.dp)
                    )
                }
            }
        }
    }
    LaunchedEffect(
        splashViewModel.checked,
        splashViewModel.cookieValid,
        splashViewModel.checkingCookkie
    ) {
        if (splashViewModel.checked && !splashViewModel.checkingCookkie) {
            // 前往主页
            if (splashViewModel.cookieValid) {
                navController.navigate("index") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            } else {
                // 登录
                navController.navigate("login") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            }
        }
    }
}