package com.rerere.iwara4a.ui.screen.like

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.rerere.iwara4a.ui.public.FullScreenTopBar

@Composable
fun LikeScreen(navController: NavController){
    Scaffold(
        topBar = {
            FullScreenTopBar(
                title = {
                    Text(text = "喜欢")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Text(text = "还没写 \uD83D\uDE05")
    }
}