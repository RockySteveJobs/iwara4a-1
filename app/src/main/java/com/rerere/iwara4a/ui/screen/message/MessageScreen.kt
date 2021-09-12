package com.rerere.iwara4a.ui.screen.message

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.rerere.iwara4a.ui.local.LocalNavController
import com.rerere.iwara4a.ui.public.ComposeWebview
import com.rerere.iwara4a.ui.public.FullScreenTopBar

@Composable
fun MessageScreen(
    messageViewModel: MessageViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    var title by remember {
        mutableStateOf("私聊消息")
    }
    Scaffold(topBar = {
        FullScreenTopBar(
            title = {
                Text(text = title)
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        )
    }) {
        ComposeWebview(
            link = "https://ecchi.iwara.tv/messages",
            session = messageViewModel.sessionManager.session
        ) {
            title = it
        }
    }
}